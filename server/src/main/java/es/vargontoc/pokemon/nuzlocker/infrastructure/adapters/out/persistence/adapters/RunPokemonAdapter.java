package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.adapters;


import java.util.List;

import org.springframework.stereotype.Repository;

import es.vargontoc.framework.persistence.BaseAdapter;
import es.vargontoc.framework.persistence.BaseRepository;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persitence.RunPokemonPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunPokemon;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.RunPokemonJpaEntity;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers.RunPokemonMapper;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.repositories.RunPokemonRepository;

@Repository
public class RunPokemonAdapter extends BaseAdapter<Long, RunPokemonJpaEntity, RunPokemon, RunPokemonMapper> implements RunPokemonPort  {

    protected RunPokemonAdapter(BaseRepository<Long, RunPokemonJpaEntity> repository, RunPokemonMapper mapper) {
        super(repository, mapper);
        
    }

    @Override
    public List<RunPokemon> findByRunIdOrderByCaughtAtStep(Long runId) {
        List<RunPokemonJpaEntity> entities =((RunPokemonRepository)repository).findByRunIdOrderByCaughtAtStep(runId);
        return mapper.toDtoList(entities);
    }
    
}
