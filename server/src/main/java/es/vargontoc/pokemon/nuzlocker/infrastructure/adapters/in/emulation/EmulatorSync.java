package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.emulation;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.vargontoc.pokemon.nuzlocker.application.dto.EventResult;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.NuzlockeUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.RunPokemonPort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.SpeciePort;
import es.vargontoc.pokemon.nuzlocker.domain.models.ActiveGame;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.EncounterResolution;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PartyMon;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PendingEncounter;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.CompletionCondition;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.CompletionType;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunPokemon;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.RuleDecision;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.web.dto.EncounterRequest;

@Service
public class EmulatorSync {


    static final Logger log = LoggerFactory.getLogger(EmulatorSync.class);
    
    final NuzlockeUseCase nuzlocke;
    final RunPokemonPort pokemon;
    final SpeciePort species;
    final ActiveGame game;

    public EmulatorSync(NuzlockeUseCase nuzlocke, RunPokemonPort pokemon, SpeciePort species, ActiveGame game) {
        this.nuzlocke = nuzlocke;
        this.pokemon = pokemon;
        this.species = species;
        this.game = game;
    }


    public void event(Long runIsd, CompletionType type, String value) {
        EventResult result = nuzlocke.applyEvent(runIsd, new CompletionCondition(type, value));
        if(!result.completedSteps().isEmpty())
            log.info("Emulador: {} {} completa {}", type, value, result.completedSteps());
    }
    
    public String starterOf(Long runId) {
        return nuzlocke.state(runId).starter();
    }

    public Optional<String> slugOf(int nationalDex) {
        return nationalDex <= 0 ? Optional.empty() : species.findById(Long.valueOf(nationalDex)).map(s -> s.slug());
    }
    
    public void reacordEncounter(Long runId, EncounterResolution resolution) {
        PendingEncounter e = resolution.encounter();
        EncounterRequest request = new EncounterRequest(e.locationId(), e.specie(), e.shiny(), e.level(), null, resolution.caught());
        RuleDecision decision = nuzlocke.evaluateEncounter(runId, request.to());
        if(!decision.allowed()){
            if(resolution.caught())
                log.warn("CAPTURA ILEGAL de {} en{}: {}", e.specie(), e.locationId(), decision.message());
            else {
                log.info("Encuentro de {} sin efecto en la partida: {}", e.specie(), decision.message());
            }
            return;
        }
        nuzlocke.recordEncounter(runId, request.toPokemon(), request.caught());
        log.info("Encuentro registrado {} en {} ({})", e.specie(), e.locationId(), resolution.caught() ? "capturado" :  "no capturado");
    }

    void synParty(Long runId, List<PartyMon> party) {
        List<PartyMon> valid = party.stream().filter(p -> p.valid()).toList();
        if(valid.isEmpty())
            return;

        if(nuzlocke.state(runId).starter() == null){
            PartyMon first = valid.getFirst();
            slugOf(first.species()).filter(game.manifest().starters()::contains).ifPresent(slug -> {
                nuzlocke.chooseStarter(runId, slug, first.nickname());
                log.info("Emulador: inicial elegido {}", slug);
            });
        }

        List<RunPokemon> owned = pokemon.findByRunIdOrderByCaughtAtStep(runId);
        for(PartyMon mon: valid) {
            Optional<RunPokemon> known = owned.stream().filter(p -> mon.identityKey().equals(p.identityKey())).findFirst();
            if(known.isEmpty())  {
                link(runId, owned, mon);
                continue;
            }

            RunPokemon p = known.get();
            if(!p.isAlive())
                continue;

            if(mon.level() != p.level())
                nuzlocke.updateLevel(runId, p.id(), mon.level());

            if(mon.hp() == 0){
                nuzlocke.faint(runId, p.id(), "Debilitado (detectado por el emulador)");
                log.warn("Emulador: {} ha muerto", p.displayName());
            }

        }
    }


    void link(Long runId, List<RunPokemon> owned, PartyMon mon) {
        Optional<String> slug = slugOf(mon.species());
        Optional<RunPokemon> candidate = owned.stream().filter(p -> p.id() == null && p.isAlive() && slug.map(p.specie()::equals).orElse(false)).reduce((f, s) -> s);
        candidate.ifPresentOrElse(
            p -> nuzlocke.linkIdentity(runId, p.id(), mon.identityKey()),
            () -> log.debug("Pokémon en el equipo sin registrar en la partida: {}", slug.orElse("#" + mon.species())));
    }

}
