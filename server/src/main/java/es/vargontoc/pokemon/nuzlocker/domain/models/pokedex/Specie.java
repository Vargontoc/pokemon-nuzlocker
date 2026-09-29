package es.vargontoc.pokemon.nuzlocker.domain.models.pokedex;

import java.util.List;

public record Specie(Long id, String slug, String name, List<String> types, BaseStats stats, List<String> abilities, String family, List<LearnEntry> learnSet)  {
}
