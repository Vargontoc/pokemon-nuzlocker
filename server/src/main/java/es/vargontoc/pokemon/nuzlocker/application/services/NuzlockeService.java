package es.vargontoc.pokemon.nuzlocker.application.services;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.vargontoc.pokemon.nuzlocker.application.dto.EventResult;
import es.vargontoc.pokemon.nuzlocker.application.dto.PokemonView;
import es.vargontoc.pokemon.nuzlocker.application.dto.RunStateView;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.LevelCapRuleUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.NuzlockeUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.StepProgressionUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.GuideCatalogPort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.RunEncounterPort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.RunNuzlockePort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.RunPokemonPort;
import es.vargontoc.pokemon.nuzlocker.domain.exceptions.RuleViolationException;
import es.vargontoc.pokemon.nuzlocker.domain.models.ActiveGame;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.CompletionCondition;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Location;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Step;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.EncounterRecord;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunNuzlocke;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunPokemon;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.EncounterOutcomeType;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.LevelCapModeType;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.RunPokemonStatusType;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.NuzlockeRules;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.RuleDecision;
import es.vargontoc.pokemon.nuzlocker.domain.services.NuzlockeRulesEngine;

@Service
@Transactional
public class NuzlockeService implements NuzlockeUseCase, StepProgressionUseCase, LevelCapRuleUseCase {


    private final ActiveGame game;
    private final GuideCatalogPort catalog;
    private final NuzlockeRulesEngine engine;

    private final RunNuzlockePort nuzlockePort;
    private final RunPokemonPort pokemonPort;
    private final RunEncounterPort encounterPort;

    public NuzlockeService(ActiveGame game, GuideCatalogPort catalog, NuzlockeRulesEngine engine, RunNuzlockePort nuzlockePort, RunPokemonPort pokemonPort, RunEncounterPort encounterPort) {

        this.game = game;
        this.catalog = catalog;
        this.engine = engine;
        this.nuzlockePort = nuzlockePort;
        this.pokemonPort = pokemonPort;
        this.encounterPort = encounterPort;
    }

    @Override
    public RunStateView create(NuzlockeRules rules) {
        if(rules == null)
            rules = NuzlockeRules.standard();
        Set<String> unsupported = new LinkedHashSet<>(rules.enabledOptionalRules());
        unsupported.retainAll(game.manifest().unsupportedRules());

        if(!unsupported.isEmpty())
            throw new IllegalArgumentException("%s no admite las reglas %s".formatted(game.manifest().name(), unsupported));

        Step first = catalog.steps().getFirst();
        
        RunNuzlocke stored = nuzlockePort.save(RunNuzlocke.init(game.id(), rules, first.order(), first.locationId()));
        return view(stored);
    }

    @Override
    @Transactional(readOnly = true)
    public RunStateView state(Long runId) {
        return view(load(runId));
    }

    
    @Override
    public EventResult chooseStarter(Long id, RunPokemon pokemon) {
        RunNuzlocke run = load(id);
        if(!game.manifest().starters().contains(pokemon.specie()))
            throw new IllegalArgumentException("Inicial no válido: " + pokemon.specie());

        pokemonPort.save(new RunPokemon(null, run.id(), pokemon.specie(), pokemon.nickname(), 5, RunPokemonStatusType.PARTY, false, null, run.currentStepOrder(), null, null));

        var stored = nuzlockePort.save(run.starter(pokemon.specie()).addFlag("HAS_STARTER")); //# persistir en repositorio
        return new EventResult(advance(stored), view(stored));
    }

    @Override
    public EventResult applyEvent(Long id, CompletionCondition event) {
        RunNuzlocke run = load(id);

        RunNuzlocke applied = null;
        switch(event.type()) {
            case FLAG, EVENT -> applied =  run.addFlag(event.value());
            case BADGE -> applied = run.addBadge(event.value());
            case LOCATION_REACHED -> applied = run.moveTo(requireLocation(event.value()).id());
            case TRAINER_DEFEATED -> {
                catalog.trainer(event.value()).orElseThrow(() -> new IllegalArgumentException("Entrenador desconocido: " + event.value()));
                applied =run.addDefeatedTrainer(event.value());
            }
        }

        nuzlockePort.save(applied);

        return new EventResult(advance(applied), view(applied));
    }
    
    @Override
    @Transactional(readOnly =  true)
    public RuleDecision evaluateEncounter(Long runId, EncounterRecord request) {
        RunNuzlocke run = load(runId);
        return engine.evaluateEncounter(run, requireLocation(request.locationId()), request.specie(), request.shiny(), List.of(), List.of());
        
    }

    @Override
    public RunStateView recordEncounter(Long runId, RunPokemon encounter, boolean caught) {
        RunNuzlocke run = load(runId);

        EncounterOutcomeType outcome = caught ? EncounterOutcomeType.CAUGHT : EncounterOutcomeType.FAILED;
        RuleDecision decision = evaluateEncounter(runId, new EncounterRecord(runId, encounter.locationId(), encounter.specie(), encounter.shiny(), outcome, true, run.currentStepOrder()));
        if(!decision.allowed())
            throw new RuleViolationException(decision);

        encounterPort.save(new EncounterRecord(run.id(), encounter.locationId(),encounter.specie(),encounter.shiny(),outcome,decision.consumeZone(),run.currentStepOrder()));

        if(caught){
            long inParty = pokemonPort.findByRunIdOrderByCaughtAtStep(runId).stream().filter(p -> p.status() == RunPokemonStatusType.PARTY).count();
            RunPokemonStatusType destination = inParty  < 6 ? RunPokemonStatusType.PARTY : RunPokemonStatusType.BOX;
        
            pokemonPort.save(new RunPokemon(null, run.id(), encounter.specie(), encounter.nickname(), encounter.level(), destination, encounter.shiny(), encounter.locationId(), run.currentStepOrder(), null, null));
        }
        return view(run);
    }


    
    @Override
    public RunStateView faint(Long runId, Long pokemonId, String causeOfDeath) {
        RunNuzlocke run = load(runId);
        RunPokemon fainted = pokemonPort.findById(pokemonId).filter(p -> p.runId() == runId).orElseThrow();

        pokemonPort.save(fainted.die(causeOfDeath));
        boolean anyAlive = pokemonPort.findByRunIdOrderByCaughtAtStep(runId).stream().anyMatch(p -> p.isAlive());
        if(!anyAlive)
            return view(nuzlockePort.save(run.lose()));
        return view(run);
    }

    @Override
    public RunStateView updateLevel(Long runId, Long pokemonId, int level) {
        RunNuzlocke run = load(runId);
        RunPokemon rp = pokemonPort.findById(pokemonId).filter(p -> p.runId() == runId).orElseThrow();

        pokemonPort.save(rp.levelUp(level));

        return view(run);
    }

    @Override
    public void linkIdentity(Long runId, Long pokemonId, String identityKey) {
        RunPokemon rp =  pokemonPort.findById(pokemonId).filter(p -> p.runId() == runId).orElseThrow();
        pokemonPort.save(rp.identity(identityKey));
    }

    @Override
    public RuleDecision evaluateLevel(Long runId, int level) {
        RunNuzlocke run = load(runId);
        return engine.evaluateLevel(run.rules(), level, capFor(run));
    }


    @Override
    public List<String> advance(RunNuzlocke run) {List<String> completed = new ArrayList<>();
        Step step = currentStep(run);

        while (step.completeWhen() != null && isSatisfied(step.completeWhen(), run)) {
            run.addUnlocks(step.unlocks());
            completed.add(step.id());
            if (step.next() == null) {
                break;
            }
            step = catalog.step(step.next()).orElseThrow();
            run.goToStep(step.order());
        }
        return completed;
    }

    @Override
    public Step currentStep(RunNuzlocke run) {
        return catalog.stepByOrder(run.currentStepOrder())
                .orElseThrow(() -> new IllegalStateException("Paso inexistente: " + run.currentStepOrder()));
    }

    
    @Override
    public OptionalInt capFor(RunNuzlocke run) {
        if(run.rules().levelCap() == LevelCapModeType.NONE)
            return OptionalInt.empty();

        int nextGym = run.badges().size() + 1;

        return catalog.locations().stream()
            .map(l -> l.gym())
            .filter(g -> g != null && g.number() == nextGym)
            .findFirst()
            .flatMap(g -> catalog.trainer(g.leaderTrainerId()))
            .map(l -> l.team().stream().mapToInt(tp -> tp.level()).max())
            .orElse(OptionalInt.empty());
    }

    private RunStateView view(RunNuzlocke run) {
        List<RunPokemon> all = List.of(); //# Llamar al repositorio
        List<String> usedZones = List.of(); //# llamar al repositorio

        Step step = currentStep(run);
        OptionalInt cap = capFor(run);

        return new RunStateView(run.id(), run.gameId(), run.status(), run.rules(), run.starter(),
        new RunStateView.CurrentStep(step.id(), step.order(), step.objective()),
        run.currentLocationId(), run.badges(), run.flags(), run.unlocks(),
        cap.isPresent() ? cap.getAsInt() : null,
        byStatus(all, RunPokemonStatusType.PARTY), byStatus(all, RunPokemonStatusType.BOX), byStatus(all, RunPokemonStatusType.DEATH), usedZones);
    }

    

    private List<PokemonView> byStatus(List<RunPokemon> all, RunPokemonStatusType status) {
        return all.stream().filter(p -> p.status() == status).map(PokemonView::of).toList();
    }

    private Location requireLocation(String value) {
        return catalog.location(value).orElseThrow(() -> new IllegalArgumentException("Localización desconocida: " + value));
    }


    private RunNuzlocke load(Long id) {
        RunNuzlocke run = nuzlockePort.findById(id).orElseThrow();
        if(!game.id().equals(run.gameId()))
            throw new IllegalStateException("La partida es de '%s' y el backend tiene  cargado '%s' (nuzlocke.game)".formatted(run.gameId(), game.id()));
        return run;
    }


    static boolean isSatisfied(CompletionCondition condition, RunNuzlocke run) {
        return switch (condition.type()) {
            case FLAG, EVENT -> run.flags().contains(condition.value());
            case BADGE -> run.badges().contains(condition.value());
            // Localización ACTUAL, no visitada alguna vez: al volver a un lugar no se completan pasos futuros
            case LOCATION_REACHED -> condition.value().equals(run.currentLocationId());
            case TRAINER_DEFEATED -> run.defeatedTrainers().contains(condition.value());
        };
    }









}
