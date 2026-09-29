package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers;

import org.springframework.stereotype.Component;

import es.vargontoc.framework.mapper.BaseMapper;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.EncounterRecord;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.RunEncounterJpaEntity;

@Component
public class RunEncounterMapper extends BaseMapper<RunEncounterJpaEntity, EncounterRecord> {

    @Override
    public EncounterRecord toDto(RunEncounterJpaEntity entity) {
        return new EncounterRecord(entity.getRunId(), entity.getLocation(), entity.getSpecie(), entity.isShiny(), entity.getOutcome(), entity.isConsumesZone(), entity.getStepOrder());
    }

    @Override
    public RunEncounterJpaEntity toEntity(EncounterRecord dto) {
        RunEncounterJpaEntity entity = new RunEncounterJpaEntity();
        entity.of(dto);
        return entity;
    }
    
}
