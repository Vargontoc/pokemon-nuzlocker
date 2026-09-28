package es.vargontoc.pokemon.nuzlocker.application.services;

import org.springframework.stereotype.Service;

import es.vargontoc.pokemon.nuzlocker.application.ports.in.NuzlockeUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.ai.RagSearchPort;

@Service
public class NuzlockeService implements NuzlockeUseCase {

    private final RagSearchPort rag;

    public NuzlockeService(RagSearchPort rag) {
        this.rag = rag;
    }


    @Override
    public void startGame(Long runId) {
        rag.reindex();   
    }
    
}
