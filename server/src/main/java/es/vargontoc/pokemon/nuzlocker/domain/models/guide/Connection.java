package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.DirectionType;

@JsonInclude(Include.NON_NULL)
public record Connection(String to, DirectionType direction, List<Requirement> requires) {}
