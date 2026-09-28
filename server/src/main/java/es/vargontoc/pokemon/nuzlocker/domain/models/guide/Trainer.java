package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.TrainerType;

@JsonInclude(Include.NON_NULL)
public record Trainer(String id, String name, TrainerType type, String locationId, boolean optional, boolean isGymLeader, List<TrainerPokemon> team) {
}
