package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.adapters;

import org.springframework.stereotype.Repository;

import es.vargontoc.framework.persistence.BaseAdapter;
import es.vargontoc.framework.persistence.BaseRepository;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.AbilityPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Ability;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.AbilityJpaEntity;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers.AbilityMapper;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.repositories.AbilityRepository;

@Repository
public class AbilityAdapter extends BaseAdapter<Long, AbilityJpaEntity, Ability, AbilityMapper> implements AbilityPort {

    protected AbilityAdapter(BaseRepository<Long, AbilityJpaEntity> repository, AbilityMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public void deleteAbove(int abilities) {
        ((AbilityRepository)repository).deleteAbove(abilities);
    }
    
}
