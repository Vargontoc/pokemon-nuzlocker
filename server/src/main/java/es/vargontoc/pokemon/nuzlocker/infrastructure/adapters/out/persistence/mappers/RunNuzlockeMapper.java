package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers;

import org.springframework.stereotype.Component;

import es.vargontoc.framework.mapper.BaseMapper;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunNuzlocke;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.RunNuzlockeJpaEntity;

@Component
public class RunNuzlockeMapper extends BaseMapper<RunNuzlockeJpaEntity, RunNuzlocke>{

    @Override
    public RunNuzlocke toDto(RunNuzlockeJpaEntity entity) {
        return new RunNuzlocke(entity.getId(), entity.getGameId(), entity.getStatus(), entity.getStarter(), entity.getCurrentStepOrder(), entity.getCurrentLocation(), entity.getBadges(), entity.getFlags(), entity.getDefeatedTrainers(), entity.getUnlocks(), entity.getRules());
    }

    @Override
    public RunNuzlockeJpaEntity toEntity(RunNuzlocke dto) {
        RunNuzlockeJpaEntity entity = new RunNuzlockeJpaEntity();
        entity.of(dto);
        return entity;
    }
    
}
