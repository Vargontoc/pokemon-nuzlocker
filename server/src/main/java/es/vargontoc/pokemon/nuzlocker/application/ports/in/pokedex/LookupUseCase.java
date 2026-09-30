package es.vargontoc.pokemon.nuzlocker.application.ports.in.pokedex;

import java.util.Optional;

import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.LookupResult;

public interface LookupUseCase {
    
    Optional<LookupResult> lookup(String name);

    void invalidate();
}
