package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import java.util.List;


import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.EncounterMethodType;


public record Encounterslot(EncounterMethodType method, String specie, Integer minLevel, Integer maxLevel, List<Requirement> requires, String exclusiveVersion) {}
