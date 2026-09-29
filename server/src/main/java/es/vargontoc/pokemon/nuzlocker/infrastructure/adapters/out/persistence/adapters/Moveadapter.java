package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.adapters;

import org.springframework.stereotype.Repository;

import es.vargontoc.framework.persistence.BaseAdapter;
import es.vargontoc.framework.persistence.BaseRepository;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.MovePort;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Move;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.MoveJpaEntity;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers.MoveMapper;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.repositories.MoveRepository;

@Repository
public class MoveAdapter  extends BaseAdapter<Long, MoveJpaEntity, Move, MoveMapper> implements MovePort {

    protected MoveAdapter(BaseRepository<Long, MoveJpaEntity> repository, MoveMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public void deleteAbove(int moves) {
        ((MoveRepository)repository).deleteAbove(moves);
    }
    
}
