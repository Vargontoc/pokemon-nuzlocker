package es.vargontoc.pokemon.nuzlocker.application.services;


import java.util.Optional;

import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.dto.PokemonView;
import es.vargontoc.pokemon.nuzlocker.application.dto.RunStateView;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.agents.AgentDecisionValidationUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.agents.EncounterCheckUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.GuideCatalogPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.ActiveGame;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.NuzlockeDecision;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.Situation;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.Validation;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Trainer;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.RuleDecision;

@Component
public class AgentDecisionValidator implements AgentDecisionValidationUseCase {
    
    final GuideCatalogPort catalog;
    final ActiveGame game;

    public AgentDecisionValidator(GuideCatalogPort catalog, ActiveGame game) {
        this.catalog = catalog;
        this.game = game;
    }

    @Override
    public Validation validate(RunStateView state, Situation situation, NuzlockeDecision decision,
            EncounterCheckUseCase encounters) {
        if(decision.action() == null)
            return Validation.reject("La decisión no indica ninguna acción");

        if(!situation.type().allowed().contains(decision.action()))
            return Validation.reject("La acción %s no está permitida en %s; opciones: %s".formatted(decision.action(), situation.type(), situation.type().allowed()));

        return switch(decision.action()){
            case CHOOSE_STARTER -> game.manifest().starters().contains(decision.target()) ? Validation.ok() : Validation.reject("El inicial debe ser uno de " + game.manifest().starters());
            case CATCH -> {
                RuleDecision rule = encounters.evaluate(situation);
                yield rule.allowed() ? Validation.ok() : Validation.reject(rule.message());
            }
            case AVOID -> trainer(situation).map(t -> t.optional() ? Validation.ok() : Validation.reject("%s es obligatorio: no se puede evitar".formatted(t.name()))).orElse(Validation.ok());
            case FIGHT -> validateFight(state, situation);
            case TRAIN -> validateTrain(state);
            case DONT_CATCH, PROCEED -> Validation.ok();
        };
    }



    Validation validateFight(RunStateView state, Situation situation) {
        boolean againstLeader = trainer(situation).map(t -> t.isGymLeader()).orElse(false);

        if(againstLeader && state.levelCap() != null)
        {
            for(PokemonView p : state.party()){
                if(p.level() > state.levelCap()) {
                    return Validation.reject("%s (nivel %d) supera el límite de nivel %d para el líder".formatted(p.nickname() != null ? p.nickname() : p.specie(), p.level(), state.levelCap()));
                }
            }
        }

        return Validation.ok();
    }

    Validation validateTrain(RunStateView state) {
        if(state.levelCap() != null && !state.party().isEmpty() && state.party().stream().allMatch(p -> p.level() >= state.levelCap()))
            return Validation.reject("Todo el equipo está ya en el límite de nivel " + state.levelCap());
        return Validation.ok();
    }

    Optional<Trainer> trainer(Situation situation) {
        return situation.trainer() == null ? Optional.empty() : catalog.trainer(situation.trainer());
    }


}
