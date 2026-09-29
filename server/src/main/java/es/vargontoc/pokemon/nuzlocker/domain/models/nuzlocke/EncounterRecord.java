package es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.EncounterOutcomeType;

public record EncounterRecord(long runId, String locationId, String specie, boolean shiny, EncounterOutcomeType outcome, boolean consumeZone, int stepOrder) {
}
