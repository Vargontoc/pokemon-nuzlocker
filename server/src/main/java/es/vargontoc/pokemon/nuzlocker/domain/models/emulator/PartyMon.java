package es.vargontoc.pokemon.nuzlocker.domain.models.emulator;

import java.util.List;

public record PartyMon(String identityKey, int species, String nickname, int level, int hp, int maxHp, List<Integer> moves, boolean shiny, boolean valid) {
    
}
