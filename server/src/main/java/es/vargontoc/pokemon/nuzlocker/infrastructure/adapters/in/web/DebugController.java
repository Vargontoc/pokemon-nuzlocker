package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import es.vargontoc.pokemon.nuzlocker.application.services.SnapshotService;
import es.vargontoc.pokemon.nuzlocker.application.services.helper.CalibrationService;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.GameSnapshot;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PartyMon;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator.EmulationGameData;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator.ProfileRegistry;



@RestController
@RequestMapping("/debug")
public class DebugController {
    

    private final ProfileRegistry profiles;
    private final SnapshotService snapshots;
    private final EmulationGameData gameData;
    private final CalibrationService calibration;

    public DebugController(CalibrationService calibration, EmulationGameData data, SnapshotService snapshots,  ProfileRegistry registry) {
        this.snapshots = snapshots;
        this.profiles = registry;
        this.gameData = data;
        this.calibration = calibration;
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

    /** Marca el estado actual de las flags. Llama justo antes de la acción que quieres calibrar. */
    @PostMapping("/emulator/calibration/flags")
    void markFlags() throws IOException {
        calibration.markFlags();
    }

    /** Compara las flags actuales contra la última marca: los flagId que salen son los candidatos. */
    @GetMapping("/emulator/calibration/flags")
    List<CalibrationService.FlagDiff> diffFlags() throws IOException {
        return calibration.diffFlags();
    }

    @DeleteMapping("/emulator/calibration/flags")
    void resetFlags() {
        calibration.resetFlags();
    }

        /** Perfil propuesto, listo para guardar en games/<id>/profiles/<fileName>. */
    @GetMapping("/emulator/calibration/profile")
    CalibrationService.ProfileSuggestion profile() throws IOException {
        return calibration.suggestProfile();
    }
}
