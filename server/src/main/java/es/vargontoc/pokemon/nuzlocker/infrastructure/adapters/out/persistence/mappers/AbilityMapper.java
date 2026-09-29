package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers;

import org.springframework.stereotype.Component;

import es.vargontoc.framework.mapper.BaseMapper;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Ability;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.AbilityJpaEntity;

@Component
public class AbilityMapper extends BaseMapper<AbilityJpaEntity, Ability> {

    @Override
    public Ability toDto(AbilityJpaEntity entity) {
        return new Ability(entity.getId(), entity.getSlug(), entity.getName(), entity.getDescription());
    }

    @Override
    public AbilityJpaEntity toEntity(Ability dto) {
        AbilityJpaEntity entity = new AbilityJpaEntity();
        entity.of(dto);
        return entity;
    }
    
}
