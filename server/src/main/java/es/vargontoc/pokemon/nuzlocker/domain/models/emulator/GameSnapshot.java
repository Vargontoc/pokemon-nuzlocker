package es.vargontoc.pokemon.nuzlocker.domain.models.emulator;

import java.time.Instant;
import java.util.List;

public record GameSnapshot(String romKey, List<PartyMon> party, PartyMon enemyLead, boolean trainerBattle, int mapGroup, int mapNum, byte[] flags, Instant at) {
    public boolean isFlagSet(int flagId) {
        int index = flagId / 8;
        return index < flags.length && ((flags[index] >> (flagId & 8)) & 1) == 1;
    }
}
