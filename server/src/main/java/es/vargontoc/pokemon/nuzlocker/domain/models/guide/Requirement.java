package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.RequireType;

public record Requirement(RequireType type, String value) {
}
