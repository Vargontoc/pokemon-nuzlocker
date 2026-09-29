package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.vargontoc.framework.ai.model.VectorHit;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.GuideSearchUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.transport.EmulatorTransportPort;
import es.vargontoc.pokemon.nuzlocker.application.services.GuideContextService;
import es.vargontoc.pokemon.nuzlocker.application.services.SnapshotService;
import es.vargontoc.pokemon.nuzlocker.application.services.helper.CalibrationService;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.GameSnapshot;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PartyMon;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.StepContext;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator.EmulationGameData;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator.ProfileRegistry;



@RestController
@RequestMapping("/debug")
public class DebugController {
    
    private final GuideContextService service;
    private final GuideSearchUseCase search;
    private final ProfileRegistry profiles;
    private final EmulatorTransportPort transport;
    private final SnapshotService snapshots;
    private final EmulationGameData gameData;
    private final CalibrationService calibration;

    public DebugController(GuideContextService service, CalibrationService calibration, EmulationGameData data, SnapshotService snapshots,  GuideSearchUseCase search, ProfileRegistry registry, EmulatorTransportPort transportPort) {
        this.service = service;
        this.snapshots = snapshots;
        this.search = search;
        this.profiles = registry;
        this.transport = transportPort;
        this.gameData = data;
        this.calibration = calibration;
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

    public record SnapshotView(String romKey, int mapGroup, int mapNum, String mappedLocation, boolean trainerBattle,
                            List<PartyMon> party, PartyMon enemyLead, List<String> badges, List<String> flags) {}
    



    /** Perfiles de ROM disponibles para el juego activo. */
    @GetMapping("/emulator/profiles")
    java.util.Set<String> profiles() {
        return profiles.available();
    }

        /** Lectura en crudo para calibrar direcciones y mapas: pasea por el juego y compara. */
    @GetMapping("/emulator/snapshot")
    SnapshotView snapshot() throws IOException {
        GameSnapshot s = snapshots.read();
        return new SnapshotView(s.romKey(), s.mapGroup(), s.mapNum(),
                gameData.locationFor(s.mapGroup(), s.mapNum()).orElse(null), s.trainerBattle(), s.party(), s.enemyLead(),
                gameData.badges().entrySet().stream().filter(e -> s.isFlagSet(e.getValue())).map(Map.Entry::getKey).toList(),
                gameData.flags().entrySet().stream().filter(e -> s.isFlagSet(e.getValue())).map(Map.Entry::getKey).toList());
    }


    record PressRequest(String key, int frames) {}

    /** Prueba de entrada: {"key": "A", "frames": 6} */
    @PostMapping("/emulator/press")
    Map<String, String> press(@RequestBody PressRequest request) throws IOException {
        transport.press(request.key(), request.frames() > 0 ? request.frames() : 6);
        return Map.of("status", "ok");
    }
    
        /** Busca tu equipo en la memoria. */
    @GetMapping("/emulator/calibration/party")
    List<CalibrationService.PartyCandidate> party() throws IOException {
        return calibration.scanParty();
    }


    /** Ej: POST /emulator/calibrate/saveblock?location=ruta-1 (repetir en varias localizaciones) */
    @PostMapping("/emulator/calibration/saveblock")
    CalibrationService.SaveBlockCalibration saveBlock(@RequestParam String location) throws IOException {
        return calibration.narrowSaveBlock(location);
    }

    @DeleteMapping("/emulator/calibration/saveblock")
    void resetSaveBlock() {
        calibration.resetSaveBlock();
    }

        /** Perfil propuesto, listo para guardar en games/<id>/profiles/<fileName>. */
    @GetMapping("/emulator/calibration/profile")
    CalibrationService.ProfileSuggestion profile() throws IOException {
        return calibration.suggestProfile();
    }
}
