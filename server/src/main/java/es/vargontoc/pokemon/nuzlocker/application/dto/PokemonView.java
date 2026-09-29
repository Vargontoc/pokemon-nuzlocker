package es.vargontoc.pokemon.nuzlocker.application.dto;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunPokemon;

public record PokemonView(long id, String specie, String nickname, int level, boolean shiny, String locationId, String causeofDeath) {

    public static PokemonView of(RunPokemon p) {
        return new PokemonView(p.id(), p.specie(), p.nickname(), p.level(), p.shiny(), p.locationId(), p.causeofDeath());
    }
}
