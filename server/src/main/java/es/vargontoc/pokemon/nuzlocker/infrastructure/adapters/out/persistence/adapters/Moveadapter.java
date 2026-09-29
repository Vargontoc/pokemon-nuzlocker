package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.adapters;

import org.springframework.stereotype.Repository;

import es.vargontoc.framework.persistence.BaseAdapter;
import es.vargontoc.framework.persistence.BaseRepository;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persitence.MovePort;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Move;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.MoveJpaEntity;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers.MoveMapper;

@Repository
public class Moveadapter  extends BaseAdapter<Long, MoveJpaEntity, Move, MoveMapper> implements MovePort {

    protected Moveadapter(BaseRepository<Long, MoveJpaEntity> repository, MoveMapper mapper) {
        super(repository, mapper);
    }
    
}
