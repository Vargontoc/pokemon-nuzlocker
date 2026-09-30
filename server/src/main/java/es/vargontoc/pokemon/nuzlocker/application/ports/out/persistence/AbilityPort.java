package es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence;

import java.util.List;

import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Ability;

public interface AbilityPort {

    long count();

    void deleteAbove(int abilities);

    Ability save(Ability entity);

    List<Ability> findAll();
}
