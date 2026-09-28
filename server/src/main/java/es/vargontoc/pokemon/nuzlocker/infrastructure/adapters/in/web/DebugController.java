package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.vargontoc.pokemon.nuzlocker.application.services.GuideContextService;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.StepContext;


@RestController
@RequestMapping("/debug")
public class DebugController {
    
    private final GuideContextService service;
    public DebugController(GuideContextService service) {
        this.service = service;
    }

    @GetMapping("/guide/{order}")
    public StepContext getMethodName(@PathVariable("order") int order) {
        return service.contextFor(order).orElse(null);
    }
    
}
