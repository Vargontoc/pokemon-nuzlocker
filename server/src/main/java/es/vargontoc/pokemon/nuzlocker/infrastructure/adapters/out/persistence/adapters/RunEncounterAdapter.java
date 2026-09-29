package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.adapters;

import es.vargontoc.framework.persistence.BaseAdapter;
import es.vargontoc.framework.persistence.BaseRepository;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persitence.RunEncounterPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.EncounterRecord;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.RunEncounterJpaEntity;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers.RunEncounterMapper;

public class RunEncounterAdapter extends BaseAdapter<Long, RunEncounterJpaEntity, EncounterRecord, RunEncounterMapper> implements RunEncounterPort {
    
    protected RunEncounterAdapter(BaseRepository<Long, RunEncounterJpaEntity> repository, RunEncounterMapper mapper) {
        super(repository, mapper);
    }
}
