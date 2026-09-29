package es.vargontoc.pokemon.nuzlocker.application.ports.out.persitence;

import java.util.Optional;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunNuzlocke;

public interface RunNuzlockePort {

    RunNuzlocke save(RunNuzlocke entity);

    Optional<RunNuzlocke> findById(Long id);
    
}
