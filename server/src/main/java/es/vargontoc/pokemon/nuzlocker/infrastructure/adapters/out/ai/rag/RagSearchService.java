package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.ai.rag;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import es.vargontoc.framework.ai.domain.VectorStoreClient;
import es.vargontoc.framework.ai.model.CreateDocumentForVector;
import es.vargontoc.framework.ai.model.VectorFilter;
import es.vargontoc.framework.ai.model.VectorHit;
import es.vargontoc.framework.ai.model.VectorQuery;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.GuideCatalogPort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.ai.RagSearchPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Location;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Step;

@Service
public class RagSearchService implements RagSearchPort {
    
    private static final String GUIDE_AGENT = "guide-agent";

    private static final String SOURCE = "guide-step";
    private static final String META_SOURCE = "source";
    private static final String META_GAME_ID = "game_id";
    private static final String META_STEP_ID = "stepId";
    private static final String META_STEP_ORDER = "stepOrder";
    private static final String META_LOCATION_ID = "locationId";
    private static final String META_BADGES = "badges";

    private static final int WINDOW = 3;

    private final VectorStoreClient vectors;
    private final GuideCatalogPort catalog;


    public RagSearchService(GuideCatalogPort catalog, VectorStoreClient vectors) {
        this.catalog =  catalog;
        this.vectors = vectors;
    }

    @Override
    public int reindex() {
        vectors.deleteByMetadata(GUIDE_AGENT, VectorFilter.where()
                .eq(META_SOURCE, SOURCE)
                .eq(META_GAME_ID, catalog.gameId()));
        List<CreateDocumentForVector> docs = catalog.steps().stream().filter(s -> s.isIndexable()).map(this::toDocument)
                .toList();

        vectors.add(GUIDE_AGENT, docs);
        return docs.size();
    }

    @Override
    public List<VectorHit> search(String query, int currentOrder) {
        VectorFilter filter = guideOfGame()
                .gte(META_STEP_ORDER, currentOrder)
                .lte(META_STEP_ORDER, currentOrder + WINDOW);
        return vectors.search(GUIDE_AGENT, VectorQuery.of(query).topK(4).filter(filter));
    }

    @Override
    public List<VectorHit> searchAll(String query, int topK) {
        return vectors.search(GUIDE_AGENT, VectorQuery.of(query).topK(topK).filter(guideOfGame()));
    }

    
    private VectorFilter guideOfGame() {
        return VectorFilter.where().eq(META_SOURCE, SOURCE).eq(META_GAME_ID, catalog.gameId());
    }

    private CreateDocumentForVector toDocument(Step step) {
        Location l = catalog.location(step.locationId()).orElseThrow();
        String content = "%s. Objetivo: %s.%n%s".formatted(l.name(), step.objective(), step.text());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(META_SOURCE, SOURCE);
        metadata.put(META_GAME_ID, catalog.gameId());
        metadata.put(META_STEP_ID, step.id());
        metadata.put(META_STEP_ORDER, step.order());
        metadata.put(META_LOCATION_ID, step.locationId());
        metadata.put(META_BADGES, Objects.requireNonNullElse(step.preconditions().badges(), 0));
        return new CreateDocumentForVector(
                UUID.nameUUIDFromBytes(("step: " + catalog.gameId() + ":" + step.id()).getBytes(StandardCharsets.UTF_8))
                        .toString(),
                content,
                metadata);
    }
}
