package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.tools;


import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import es.vargontoc.framework.ai.model.VectorHit;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.GuideSearchUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.GuideCatalogPort;
import es.vargontoc.pokemon.nuzlocker.application.services.GuideContextService;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Location;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.StepContext;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Trainer;

@Component
public class GuideTools {
    

    final GuideContextService context;
    final GuideSearchUseCase search;
    final GuideCatalogPort catalog;

    public GuideTools(GuideContextService context, GuideSearchUseCase search, GuideCatalogPort catalog) {
        this.context = context;
        this.search = search;
        this.catalog = catalog;
    }

    @Tool(name = "get_step_context", description = "Paso de la historia con su localización (encuentros Pokémon salvajes, objetos, conexiones) y sus entrenadores")
    public StepContext getStepContext(@ToolParam(description = "order del paso") int order){
        return context.contextFor(order).orElse(null);
    }

    @Tool(name = "search_guide", description =  "Busca en el texto de la guía, limitado al paso actual y los siguientes")
    public List<VectorHit> search(@ToolParam(description = "pregunta en español") String query, @ToolParam(description = "order del paso actual") int order){
        return search(query, order);
    }

    @Tool(name = "get_location", description = "Datos estáticos de una localización por su id")
    public Location getLocation(@ToolParam(description = "Datos estáticos de una localización por su id") String locationId) {
        return catalog.location(locationId).orElse(null);
    }

    @Tool(name = "get_trainer", description = "Equipo y datos de un entrenador por su id")
    public Trainer getTrainer(@ToolParam(description = "id del entrenador, p- ej. lider-brok") String trainerId) {
        return catalog.trainer(trainerId).orElse(null);
    }

}
