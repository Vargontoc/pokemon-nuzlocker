package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.emulation;



import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.services.AgentTurnService;
import es.vargontoc.pokemon.nuzlocker.application.services.EncounterTracker;
import es.vargontoc.pokemon.nuzlocker.application.services.SnapshotDiffer;
import es.vargontoc.pokemon.nuzlocker.application.services.SnapshotService;
import es.vargontoc.pokemon.nuzlocker.application.services.TrainerTracker;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.Situation;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.TurnResult;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.enums.SituationType;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.BridgeSignal;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.BridgeStatus;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.GameSnapshot;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PartyMon;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PendingEncounter;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.CompletionType;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator.EmulationGameData;
import jakarta.annotation.PreDestroy;

@Component
public class EmulationBridge {
    static final Logger log = LoggerFactory.getLogger(EmulationBridge.class);


    final SnapshotService snapshots;
    final SnapshotDiffer differ;
    final EmulatorSync sync;
    final EmulationGameData data;
    final AgentTurnService turns;
    final TrainerTracker trainers;
    final ExecutorService turnExecutor = Executors.newSingleThreadExecutor();

    volatile Long runId;
    volatile BridgeStatus status = BridgeStatus.disconnected("Sin leer todavia", null, null);
    volatile TurnResult lastDecision;
    volatile String currentTrainerId;

    GameSnapshot previous;
    EncounterTracker tracker = new EncounterTracker();

    public EmulationBridge(SnapshotService snapshots, SnapshotDiffer differ, EmulatorSync sync, EmulationGameData data,
            AgentTurnService turns, TrainerTracker trainers) {
        this.snapshots = snapshots;
        this.differ = differ;
        this.sync = sync;
        this.data = data;
        this.turns = turns;
        this.trainers = trainers;
    }

    public synchronized void attach(Long runId) {
        this.runId = runId;
        previous = null;
        tracker = new EncounterTracker();
        lastDecision = null;
        currentTrainerId = null;
    }

    public synchronized void detach(){
        runId = null;
    }

    public BridgeStatus stautus() {
        return status;
    }

    @Scheduled(fixedDelayString = "${nuzlocke.emulator.poll-interval-ms}")
    public synchronized void poll() {
        GameSnapshot snapshot;
        try {
            snapshot = snapshots.read();
        }catch(IOException | IllegalStateException e) {
            status = BridgeStatus.disconnected(e.getLocalizedMessage(), runId, lastDecision);
            snapshots.reset();
            log.error(e.getMessage());
            return;
        }

        Optional<String> location = data.locationFor(snapshot.mapGroup(), snapshot.mapNum());
        Long run = runId;
        if(run != null) {
            try {
                process(runId, snapshot, location);
            }catch(RuntimeException e) {
                log.warn("Error sincronizando la partida en el emulador: {}", e.getMessage());
            }
        }

        previous = snapshot;
        status = new BridgeStatus(true, null, snapshot.romKey(), run, location.orElse(null), tracker.pending().orElse(null), currentTrainerId, lastDecision, snapshot.at());
    }

    void process(Long id, GameSnapshot  snapshot, Optional<String> location) {
        tracker.observe(snapshot, location.orElse(null)).ifPresent(r -> sync.reacordEncounter(id, r));

        List<BridgeSignal> signals = differ.diff(previous, snapshot);
        for(BridgeSignal signal : signals) {
            switch (signal) {
                case BridgeSignal.FlagSet f -> sync.event(id, CompletionType.FLAG, f.flag());
                case BridgeSignal.BadgeObtained b -> sync.event(id, CompletionType.BADGE, b.badge());
                case BridgeSignal.LocationReached l -> sync.event(id, CompletionType.LOCATION_REACHED, l.locationId());
                case BridgeSignal.WildBattleStarted w -> startEncounter(id, w.enemy(), location);
                case BridgeSignal.TrainerBattleStarted t -> startTrainerBattle(id, t.team());
                case BridgeSignal.TrainerBattleWon t -> trainerDefeated(id, t.team());
            }
        }

        sync.synParty(id, snapshot.party());
    }

    void startEncounter(Long id, PartyMon enemy, Optional<String> location) {
        Optional<String> specie = sync.slugOf(enemy.species());
        if(location.isEmpty() || specie.isEmpty()){
            log.warn("Enciuentro salvaje sin localización o especie conocida (mapa sin registrar o especie #{})", enemy.species());
            return;
        }

        tracker.start(new PendingEncounter(enemy.identityKey(), location.get(), specie.get(), enemy.level(), enemy.shiny()));
        log.info("Encuentro salvaje: {} nivle {} en {}{}", specie.get(), enemy.level(), location.get(), enemy.shiny() ? " (SHINY)": "");

        // Ejecucion de los agentes
        Situation situation = new Situation(SituationType.WILD_ENCOUNTER, location.get(), specie.get(), enemy.level(), enemy.shiny(), null, "Detectado por el emulador");
        turnExecutor.submit(() -> {
            try {
                lastDecision = turns.play(id, situation);
                log.info("Decisión de poke-nuzlocker: {}", lastDecision.decision());
            }catch(RuntimeException e) {
                log.warn("Fallo al pedir a los agentes: {}", e.getMessage());
            }
        });
    }

    void startTrainerBattle(Long run, List<PartyMon> team) {
        currentTrainerId = trainers.identify(team, sync.starterOf(run)).map(t -> t.id()).orElse(null);
        if(currentTrainerId != null)
            log.info("Combate contra entrenador: {}", currentTrainerId);
        else {
            log.warn("Combate contra un entrenador que no está en la guía (o su equipo no coincide): {}}", trainers.describe(team));
        }
    }
    

    void trainerDefeated(Long run, List<PartyMon> team) {
        String trainerId = currentTrainerId != null ? currentTrainerId : trainers.identify(team, sync.starterOf(run)).map(t -> t.id()).orElse(null);
        if(trainerId != null) {
            sync.event(run, CompletionType.TRAINER_DEFEATED, trainerId);
            log.info("Entrenador derrotado: {}", trainerId);
        }
    }

    @PreDestroy
    void shutdown() {
        turnExecutor.shutdownNow();
    }

}
