package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.CompletionType;

/*
Condición para completar una paso de la guía
*/

public record CompletionCondition(CompletionType type, String value) {}
