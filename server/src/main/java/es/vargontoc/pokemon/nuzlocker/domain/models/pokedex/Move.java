package es.vargontoc.pokemon.nuzlocker.domain.models.pokedex;

import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.enums.CategoryMoveType;

public record Move(Long id, String slug, String name, String description, String type, Integer power, Integer accuracry, Integer pp, Integer effectChance, CategoryMoveType category, int priority) {
    
}
