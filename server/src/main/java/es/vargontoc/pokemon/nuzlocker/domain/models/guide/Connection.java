package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import java.util.List;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.DirectionType;

public record Connection(String to, DirectionType direction, List<Requirement> requires) {}
