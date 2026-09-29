package es.vargontoc.pokemon.nuzlocker.domain.models.emulator;

public record RomId(String gameCode, int revision) {
    public String key() { return gameCode  + "-" + revision; }

    public static RomId parse(String info){
        String[] parts = info.trim().split("\\s+");
        String code = parts[0].contains("-") ? parts[0].substring(parts[0].lastIndexOf("-") + 1) : parts[0];
        int revision = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        return new RomId(code, revision);
    }
}
