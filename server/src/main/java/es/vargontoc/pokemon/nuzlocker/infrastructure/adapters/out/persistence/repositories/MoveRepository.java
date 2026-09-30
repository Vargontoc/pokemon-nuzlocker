package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.vargontoc.framework.persistence.BaseRepository;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.MoveJpaEntity;

public interface MoveRepository  extends BaseRepository<Long, MoveJpaEntity>{
    @Modifying
    @Transactional
    @Query("delete from MoveJpaEntity e where e.id > :maxId")
    int deleteAbove(@Param("maxId") int maxId);

    Optional<MoveJpaEntity> findBySlug(String slug);
}
