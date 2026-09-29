package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import es.vargontoc.framework.persistence.BaseRepository;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities.SpecieJpaEntity;

public interface SpecieRepository  extends BaseRepository<Long, SpecieJpaEntity> {
    @Modifying
    @Transactional
    @Query("delete from SpecieJpaEntity e where e.id > :maxId")
    int deleteAbove(@Param("maxId") int maxId);
}
