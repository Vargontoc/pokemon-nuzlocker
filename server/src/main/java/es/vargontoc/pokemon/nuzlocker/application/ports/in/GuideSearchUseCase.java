package es.vargontoc.pokemon.nuzlocker.application.ports.in;

import java.util.List;

import es.vargontoc.framework.ai.model.VectorHit;

public interface GuideSearchUseCase {
    
    List<VectorHit> search(String query, int order);

    List<VectorHit> findAll(String query, int topK);
}
