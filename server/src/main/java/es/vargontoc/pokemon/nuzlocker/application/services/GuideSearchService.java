package es.vargontoc.pokemon.nuzlocker.application.services;

import java.util.List;

import org.springframework.stereotype.Service;

import es.vargontoc.framework.ai.model.VectorHit;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.GuideSearchUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.ai.RagSearchPort;

@Service
public class GuideSearchService implements GuideSearchUseCase {

    private final RagSearchPort port;

    public GuideSearchService(RagSearchPort port) {
        this.port = port;
    }

    @Override
    public List<VectorHit> search(String query, int order) {
        return port.search(query, order);
    }

    @Override
    public List<VectorHit> findAll(String query, int topK) {
        return port.searchAll(query, topK);
    }
    
}
