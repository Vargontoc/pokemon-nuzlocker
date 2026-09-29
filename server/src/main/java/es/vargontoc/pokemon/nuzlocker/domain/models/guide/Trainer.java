package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import java.util.List;


import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.TrainerType;

public record Trainer(String id, String name, TrainerType type, String locationId, boolean optional, boolean isGymLeader, List<TrainerPokemon> team) {
}
