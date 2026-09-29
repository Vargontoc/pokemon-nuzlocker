package es.vargontoc.pokemon.nuzlocker.application.ports.out.persitence;

import java.util.List;
import java.util.Optional;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunPokemon;

public interface RunPokemonPort {
    
    RunPokemon save(RunPokemon entity);

    Optional<RunPokemon> findById(Long pokemonId);

    List<RunPokemon> findByRunIdOrderByCaughtAtStep(Long runId);
}
