package es.vargontoc.pokemon.nuzlocker.application.ports.out.ai;

import java.util.List;

import es.vargontoc.framework.ai.model.VectorHit;

public interface RagSearchPort {

    int reindex();

    List<VectorHit> search(String query, int currentOrder);

    List<VectorHit> searchAll(String query, int topK);
}
