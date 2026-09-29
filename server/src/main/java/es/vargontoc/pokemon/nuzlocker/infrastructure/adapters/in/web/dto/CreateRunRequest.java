package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.web.dto;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.NuzlockeRules;

public record CreateRunRequest(String gameId, NuzlockeRules rules) {
}
