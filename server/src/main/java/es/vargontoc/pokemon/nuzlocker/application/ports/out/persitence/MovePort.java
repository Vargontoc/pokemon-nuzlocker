package es.vargontoc.pokemon.nuzlocker.application.ports.out.persitence;

import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Move;

public interface MovePort {
    
    long count();

    void deleteAbove(int moves);

    Move save(Move entity);
}
