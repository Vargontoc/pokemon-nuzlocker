package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.mappers;

import org.springframework.stereotype.Component;

import es.vargontoc.framework.mapper.BaseMapper;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunPokemon;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.RunPokemonJpaEntity;

@Component
public class RunPokemonMapper extends BaseMapper<RunPokemonJpaEntity, RunPokemon> {

    @Override
    public RunPokemon toDto(RunPokemonJpaEntity entity) {
        return new RunPokemon(entity.getId(), entity.getRunId(), entity.getSpecie(), entity.getNickName(), entity.getLevel(), entity.getStatus(), entity.isShiny(), entity.getLocation(), entity.getCaughtAtStep(), entity.getCauseOfDeath(), entity.getIdentityKey());
    }

    @Override
    public RunPokemonJpaEntity toEntity(RunPokemon dto) {
        RunPokemonJpaEntity entity = new RunPokemonJpaEntity();
        entity.of(dto);
        return entity;
    }
    
}
