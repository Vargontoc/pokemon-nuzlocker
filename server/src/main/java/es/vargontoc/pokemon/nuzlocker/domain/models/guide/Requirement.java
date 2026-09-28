package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.RequireType;

@JsonInclude(Include.NON_NULL)
public record Requirement(RequireType type, String value) {
}
