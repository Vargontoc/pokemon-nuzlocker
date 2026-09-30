package es.vargontoc.pokemon.nuzlocker.domain.models.emulator;

import java.time.Instant;
import java.util.List;

public record GameSnapshot(String romKey, List<PartyMon> party, List<PartyMon> enemyParty, boolean trainerBattle, int mapGroup, int mapNum, byte[] flags, Instant at) {
    public boolean isFlagSet(int flagId) {
        int index = flagId / 8;
        return index < flags.length && ((flags[index] >> (flagId & 7)) & 1) == 1;
    }

    public PartyMon enemyLead() {
        return enemyParty.isEmpty() ? null : enemyParty.getFirst();
    }

    public boolean enemyDefeated() {
        return enemyParty.isEmpty() && enemyParty.stream().allMatch(m -> m.valid() && m.hp() == 0);
    }
}
