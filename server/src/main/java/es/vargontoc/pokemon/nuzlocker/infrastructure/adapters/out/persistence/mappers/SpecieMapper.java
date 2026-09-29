package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers;

import org.springframework.stereotype.Component;

import es.vargontoc.framework.mapper.BaseMapper;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Specie;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.SpecieJpaEntity;

@Component
public class SpecieMapper extends BaseMapper<SpecieJpaEntity, Specie> {

    @Override
    public Specie toDto(SpecieJpaEntity entity) {
        return new Specie(entity.getId(), entity.getSlug(), entity.getName(), entity.getTypes(), entity.getStats(), entity.getAbilities(), entity.getFamily(), entity.getLearnset());
    }

    @Override
    public SpecieJpaEntity toEntity(Specie dto) {
        SpecieJpaEntity entity = new SpecieJpaEntity();
        entity.of(dto);
        return entity;
    }
    
}
