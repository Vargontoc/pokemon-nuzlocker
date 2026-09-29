package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers;

import org.springframework.stereotype.Component;

import es.vargontoc.framework.mapper.BaseMapper;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Move;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.MoveJpaEntity;

@Component
public class MoveMapper extends BaseMapper<MoveJpaEntity, Move> {

    @Override
    public Move toDto(MoveJpaEntity entity) {
        return new Move(entity.getId(), entity.getSlug(), entity.getName(), entity.getDescription(), entity.getType(), entity.getPower(), entity.getAccuracy(), entity.getPp(), entity.getEffectChance(), entity.getCategory(), entity.getPriority());
    }

    @Override
    public MoveJpaEntity toEntity(Move dto) {
        MoveJpaEntity entity = new MoveJpaEntity();
        entity.of(dto);
        return entity;
    }
    
}
