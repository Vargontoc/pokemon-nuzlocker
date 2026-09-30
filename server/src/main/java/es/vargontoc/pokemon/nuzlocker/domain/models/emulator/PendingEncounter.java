package es.vargontoc.pokemon.nuzlocker.domain.models.emulator;

public record PendingEncounter(String identityKey, String locationId, String specie, int level, boolean shiny) {
}
