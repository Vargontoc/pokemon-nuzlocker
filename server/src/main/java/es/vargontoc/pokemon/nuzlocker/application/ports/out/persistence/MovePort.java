package es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence;

import java.util.List;
import java.util.Optional;

import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Move;

public interface MovePort {
    
    long count();

    void deleteAbove(int moves);

    Move save(Move entity);

    Optional<Move> findBySlug(String slug);

    List<Move> findAll();
}
