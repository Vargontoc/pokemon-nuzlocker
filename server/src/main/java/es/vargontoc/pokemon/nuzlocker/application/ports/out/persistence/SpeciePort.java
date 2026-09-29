package es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence;

import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Specie;

public interface SpeciePort {
    
    long count();

    void deleteAbove(int species);

    Specie save(Specie entity);
}
