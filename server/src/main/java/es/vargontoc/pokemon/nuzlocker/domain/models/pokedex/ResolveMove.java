package es.vargontoc.pokemon.nuzlocker.domain.models.pokedex;

import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.enums.CategoryMoveType;

public record ResolveMove(String type, Integer power, Integer accuracy, Integer pp, Integer effectChance, CategoryMoveType category, int priority) {
    
}
