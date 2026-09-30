package es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence;

import java.util.List;
import java.util.Optional;

import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Specie;

public interface SpeciePort {
    
    long count();

    void deleteAbove(int species);

    Specie save(Specie entity);

    Optional<Specie> findBySlug(String slug);

    List<Specie> findAll();

    Optional<Specie> findById(Long id);
}
