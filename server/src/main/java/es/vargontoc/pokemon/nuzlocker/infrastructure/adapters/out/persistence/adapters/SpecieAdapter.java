package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.adapters;

import org.springframework.stereotype.Repository;

import es.vargontoc.framework.persistence.BaseAdapter;
import es.vargontoc.framework.persistence.BaseRepository;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persitence.SpeciePort;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Specie;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.SpecieJpaEntity;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers.SpecieMapper;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.repositories.SpecieRepository;

@Repository
public class SpecieAdapter  extends BaseAdapter<Long, SpecieJpaEntity, Specie, SpecieMapper> implements SpeciePort  {

    protected SpecieAdapter(BaseRepository<Long, SpecieJpaEntity> repository, SpecieMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public void deleteAbove(int species) {
        ((SpecieRepository)repository).deleteAbove(species);
    }
    
}
