package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.adapters;

import org.springframework.stereotype.Repository;

import es.vargontoc.framework.persistence.BaseAdapter;
import es.vargontoc.framework.persistence.BaseRepository;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persitence.RunNuzlockePort;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunNuzlocke;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.RunNuzlockeJpaEntity;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers.RunNuzlockeMapper;

@Repository
public class RunNuzlockeAdapter extends BaseAdapter<Long, RunNuzlockeJpaEntity, RunNuzlocke, RunNuzlockeMapper> implements RunNuzlockePort {

    protected RunNuzlockeAdapter(BaseRepository<Long, RunNuzlockeJpaEntity> repository, RunNuzlockeMapper mapper) {
        super(repository, mapper);
    }
    
}
