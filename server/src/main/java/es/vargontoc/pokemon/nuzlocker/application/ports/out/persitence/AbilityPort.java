package es.vargontoc.pokemon.nuzlocker.application.ports.out.persitence;

import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Ability;

public interface AbilityPort {

    long count();

    void deleteAbove(int abilities);

    Ability save(Ability entity);
}
