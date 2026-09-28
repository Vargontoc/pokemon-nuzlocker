package es.vargontoc.pokemon.nuzlocker.application.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.GuideCatalogPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.StepContext;

@Service
public class GuideContextService {
    

    private final GuideCatalogPort catalog;

    public GuideContextService(GuideCatalogPort catalog) {
        this.catalog = catalog;
    }

    public Optional<StepContext> contextFor(int order) {
        return catalog.stepByOrder(order)
            .map(step -> new StepContext(
                step,
                catalog.location(step.locationId()).orElseThrow(),
                step.trainers().stream().map(id -> catalog.trainer(id).orElseThrow()).toList()));
    }
}
