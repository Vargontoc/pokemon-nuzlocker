package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.repositories;

import java.util.List;

import es.vargontoc.framework.persistence.BaseRepository;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.RunPokemonJpaEntity;

public interface RunPokemonRepository extends BaseRepository<Long, RunPokemonJpaEntity> {
    List<RunPokemonJpaEntity> findByRunIdOrderByCaughtAtStep(Long runId);
}
