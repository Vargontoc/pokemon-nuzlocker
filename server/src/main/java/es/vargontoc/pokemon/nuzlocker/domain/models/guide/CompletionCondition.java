package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.CompletionType;

/*
Condición para completar una paso de la guía
*/
@JsonInclude(Include.NON_NULL)
public record CompletionCondition(CompletionType type, String value) {}
