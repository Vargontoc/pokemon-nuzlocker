package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.EncounterMethodType;

@JsonInclude(Include.NON_NULL)
public record Encounterslot(EncounterMethodType method, String specie, Integer minLevel, Integer maxLevel, List<Requirement> requires, @JsonProperty("exclusive-version") String exclusiveVersion) {}
