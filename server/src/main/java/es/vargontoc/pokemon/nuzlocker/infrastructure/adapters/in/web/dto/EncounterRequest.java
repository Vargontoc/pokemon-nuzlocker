package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.web.dto;

public record EncounterRequest(String locationId, String specie, boolean shiny, int level, String nickname, boolean caught) {
}
