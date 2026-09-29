package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.web.dto;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.CompletionType;

public record GameEventRequest(CompletionType type, String value) {
    
}
