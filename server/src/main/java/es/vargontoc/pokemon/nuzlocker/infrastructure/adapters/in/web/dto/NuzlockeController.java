package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.web.dto;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.vargontoc.pokemon.nuzlocker.application.dto.RunStateView;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.NuzlockeUseCase;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.NuzlockeRules;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.emulation.EmulationBridge;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/nuzlocke")
public class NuzlockeController {
    
    final NuzlockeUseCase nuzlocke;
    final EmulationBridge bridge;
    public NuzlockeController(NuzlockeUseCase nuzlocke, EmulationBridge bridge) {
        this.nuzlocke = nuzlocke;
        this.bridge = bridge;
    }

    @PostMapping("/new")
    public ResponseEntity<RunStateView> createRun(@RequestBody NuzlockeRules rules) {
        
        return ResponseEntity.ok(nuzlocke.create(rules));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RunStateView> getRun(@PathVariable("id") Long run) {
        return ResponseEntity.ok(nuzlocke.state(run));
    }

    @PostMapping("/{id}/run")
    public ResponseEntity<Boolean> startRun(@PathVariable("id") Long run) {
        
        bridge.attach(run);
        return ResponseEntity.ok(Boolean.TRUE);
    }
    
    @PostMapping("/{id}/stop")
    public ResponseEntity<Boolean> stopRun(@PathVariable("id") Long run) {
        bridge.detach();
        return ResponseEntity.ok(Boolean.TRUE);
    }
    
    
    

}
