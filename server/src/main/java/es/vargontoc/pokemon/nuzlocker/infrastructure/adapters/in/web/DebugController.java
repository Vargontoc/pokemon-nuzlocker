package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.vargontoc.framework.ai.model.VectorHit;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.GuideSearchUseCase;
import es.vargontoc.pokemon.nuzlocker.application.services.GuideContextService;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.StepContext;



@RestController
@RequestMapping("/debug")
public class DebugController {
    
    private final GuideContextService service;
    private final GuideSearchUseCase search;

    public DebugController(GuideContextService service, GuideSearchUseCase search) {
        this.service = service;
        this.search = search;
    }

    @GetMapping("/guide/{order}")
    public StepContext getMethodName(@PathVariable("order") int order) {
        return service.contextFor(order).orElse(null);
    }

    @GetMapping("/rag")
    public List<VectorHit> get(@RequestParam("q") String query, @RequestParam(name= "order") int order) {
        return search.search(query, order);
    }

    @GetMapping("/rag/all")
    public List<VectorHit> getAll(@RequestParam("q") String query, @RequestParam(name= "topK", defaultValue= "5") int topK) {
        return search.findAll(query, topK);
    }
    
}
