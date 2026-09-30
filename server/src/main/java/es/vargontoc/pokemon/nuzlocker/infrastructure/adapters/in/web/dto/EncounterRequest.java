package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.web.dto;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.EncounterRecord;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunPokemon;

public record EncounterRequest(String locationId, String specie, boolean shiny, int level, String nickname, boolean caught) {
    public EncounterRecord to() {
        return new EncounterRecord(-1, locationId, specie, shiny, null, false, 0);
    }

    public RunPokemon  toPokemon() {
        return new RunPokemon(null, 0, specie, nickname, level, null, shiny, locationId, 0, null, null);
    }
}
