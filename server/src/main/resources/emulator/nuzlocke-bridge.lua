-- nuzlocke-bridge.lua
-- Puente entre mGBA (0.10+) y el backend de Nuzlocke AI.
-- Cargar en mGBA: Herramientas > Scripting > Archivo > Cargar script, con la ROM ya abierta.
--
-- Protocolo de texto, una orden por línea (el backend es quien interpreta los datos):
--   PING                    -> PONG
--   INFO                    -> OK <código de juego> <revisión>     p. ej. "OK AGB-BPRE 0"
--   READ <dir hex> <bytes>  -> OK <bytes en hex>                     máximo 4096 bytes
--   PRESS <botón> <frames>  -> OK                                    A B SELECT START RIGHT LEFT UP DOWN R L

local PORT = 8888
local MAX_READ = 4096
local RELEASE_FRAMES = 4

local KEYS = {
    A = C.GBA_KEY.A, B = C.GBA_KEY.B, SELECT = C.GBA_KEY.SELECT, START = C.GBA_KEY.START,
    RIGHT = C.GBA_KEY.RIGHT, LEFT = C.GBA_KEY.LEFT, UP = C.GBA_KEY.UP, DOWN = C.GBA_KEY.DOWN,
    R = C.GBA_KEY.R, L = C.GBA_KEY.L,
}

local server = nil
local clients = {}
local buffers = {}
local nextId = 1
local keyQueue = {}
local currentKey = nil

local function toHex(bytes)
    return (bytes:gsub(".", function(c) return string.format("%02X", string.byte(c)) end))
end

local function handle(line)
    local cmd, a, b = line:match("^(%S+)%s*(%S*)%s*(%S*)")
    if cmd == "PING" then
        return "PONG"
    end
    if not emu then
        return "ERR no hay juego cargado"
    end
    if cmd == "INFO" then
        return "OK " .. emu:getGameCode() .. " " .. emu:read8(0x080000BC)
    elseif cmd == "READ" then
        local addr, len = tonumber(a, 16), tonumber(b)
        if not addr or not len or len < 1 or len > MAX_READ then
            return "ERR READ necesita dirección hex y longitud 1-" .. MAX_READ
        end
        return "OK " .. toHex(emu:readRange(addr, len))
    elseif cmd == "PRESS" then
        local key, frames = KEYS[a], tonumber(b) or 6
        if not key then
            return "ERR botón desconocido: " .. tostring(a)
        end
        table.insert(keyQueue, { key = key, frames = frames, release = RELEASE_FRAMES })
        return "OK"
    end
    return "ERR orden desconocida: " .. tostring(cmd)
end

-- Pulsaciones: cada botón se mantiene N frames y después se suelta unos frames antes del siguiente
callbacks:add("frame", function()
    if currentKey == nil and #keyQueue > 0 then
        currentKey = table.remove(keyQueue, 1)
        emu:addKey(currentKey.key)
    end
    if currentKey ~= nil then
        if currentKey.frames > 0 then
            currentKey.frames = currentKey.frames - 1
            if currentKey.frames == 0 then
                emu:clearKey(currentKey.key)
            end
        else
            currentKey.release = currentKey.release - 1
            if currentKey.release <= 0 then
                currentKey = nil
            end
        end
    end
end)

local function closeClient(id)
    if clients[id] then
        clients[id]:close()
    end
    clients[id] = nil
    buffers[id] = nil
end

local function onReceived(id)
    local sock = clients[id]
    if not sock then return end
    while true do
        local data, err = sock:receive(4096)
        if data then
            buffers[id] = (buffers[id] or "") .. data
            while true do
                local nl = buffers[id]:find("\n", 1, true)
                if not nl then break end
                local line = buffers[id]:sub(1, nl - 1):gsub("\r$", "")
                buffers[id] = buffers[id]:sub(nl + 1)
                sock:send(handle(line) .. "\n")
            end
        else
            if err ~= socket.ERRORS.AGAIN then
                console:log("Nuzlocke bridge: cliente desconectado")
                closeClient(id)
            end
            return
        end
    end
end

local function onAccept()
    local sock, err = server:accept()
    if err then return end
    local id = nextId
    nextId = nextId + 1
    clients[id] = sock
    sock:add("received", function() onReceived(id) end)
    sock:add("error", function() closeClient(id) end)
    console:log("Nuzlocke bridge: backend conectado")
end

local err
server, err = socket.bind(nil, PORT)
if err then
    console:error("Nuzlocke bridge: no se pudo abrir el puerto " .. PORT .. ": " .. tostring(err))
else
    server:listen()
    server:add("received", onAccept)
    console:log("Nuzlocke bridge escuchando en el puerto " .. PORT)
end
