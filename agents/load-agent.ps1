[CmdletBinding()]
param(
    [Parameter(Position = 0, Mandatory = $true)]
    [string]$ModelName,

    [Parameter(Position = 1, Mandatory = $true)]
    [string]$ModelFolder,

    [string]$OllamaHost = $(if ($env:OLLAMA_HOST) { $env:OLLAMA_HOST } else { "http://127.0.0.1:11434" })
)

$modelFile = Join-Path $ModelFolder "Modelfile"
$apiRoot = $OllamaHost.TrimEnd('/')
if(-not (Test-Path -LiteralPath $modelFile -PathType Leaf)){
    throw "No se encontró Modelfile en $modelFile"
}

function Get-OllamaModels {
    try {
        return Invoke-RestMethod -Method Get -Uri "${apiRoot}/api/tags"
    }catch {
        throw "No se pudo consultar los modelos Ollama en ${apiRoot}: $($_.Exception.Message)"
    }
}

function ConvertFrom-Modelfile {
    param([string]$Content)

    $result = @{}
    $parameters = @{}
    $lines = $Content -split "`r?`n"
    $i = 0
    while ($i -lt $lines.Count) {
        $trimmed = $lines[$i].Trim()
        if (-not $trimmed -or $trimmed.StartsWith('#')) { $i++; continue }

        if ($trimmed -match '(?i)^FROM\s+(.+)$') {
            $result.from = $Matches[1].Trim()
        }
        elseif ($trimmed -match '(?i)^PARAMETER\s+(\S+)\s+(.+)$') {
            $key = $Matches[1]
            $val = $Matches[2].Trim()
            $numVal = 0.0
            if ([double]::TryParse($val, [System.Globalization.NumberStyles]::Float, [System.Globalization.CultureInfo]::InvariantCulture, [ref]$numVal)) {
                $parameters[$key] = $numVal
            } else {
                $parameters[$key] = $val
            }
        }
        elseif ($trimmed -match '(?i)^(SYSTEM|TEMPLATE|LICENSE)\s+"""(.*)"""\s*$') {
            $result[$Matches[1].ToLower()] = $Matches[2]
        }
        elseif ($trimmed -match '(?i)^(SYSTEM|TEMPLATE|LICENSE)\s+"""\s*$') {
            $directive = $Matches[1].ToLower()
            $block = @()
            $i++
            while ($i -lt $lines.Count -and $lines[$i].Trim() -ne '"""') {
                $block += $lines[$i]
                $i++
            }
            $result[$directive] = ($block -join "`n").Trim()
        }
        elseif ($trimmed -match '(?i)^(SYSTEM|TEMPLATE|LICENSE)\s+"(.*)"\s*$') {
            $result[$Matches[1].ToLower()] = $Matches[2]
        }
        elseif ($trimmed -match '(?i)^(SYSTEM|TEMPLATE|LICENSE)\s+(.+)$') {
            $result[$Matches[1].ToLower()] = $Matches[2].Trim()
        }
        $i++
    }

    if ($parameters.Count -gt 0) { $result.parameters = $parameters }
    return $result
}

$models = Get-OllamaModels
$modelAvailable = $models.models | Where-Object { $_.name -eq $ModelName -or $_.name -eq "${ModelName}:latest" }
if ($modelAvailable) {
    "El modelo '$ModelName' ya existe en Ollama ($apiRoot), se sobrescribirá con el Modelfile."
}

$modelfileContent = Get-Content -LiteralPath $modelFile -Raw -Encoding UTF8
$parsed = ConvertFrom-Modelfile -Content $modelfileContent
if (-not $parsed.from) {
    throw "El Modelfile en $modelFile no contiene una directiva FROM"
}

$apiBody = [ordered]@{
    model  = $ModelName
    from   = $parsed.from
    stream = $false
}
if ($parsed.system) { $apiBody.system = $parsed.system }
if ($parsed.template) { $apiBody.template = $parsed.template }
if ($parsed.license) { $apiBody.license = $parsed.license }
if ($parsed.parameters) { $apiBody.parameters = $parsed.parameters }

$body = $apiBody | ConvertTo-Json -Depth 5

try {
    Invoke-RestMethod -Method Post -Uri "${apiRoot}/api/create" -Body $body -ContentType "application/json" | Out-Null
} catch {
    $detail = $_.ErrorDetails.Message
    if (-not $detail -and $_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $detail = $reader.ReadToEnd()
    }
    throw "No se pudo crear el modelo '$ModelName' en Ollama (${apiRoot}): $detail"
}

"Modelo '$ModelName' cargado correctamente en Ollama ($apiRoot)"