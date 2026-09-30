package es.vargontoc.pokemon.nuzlocker.application.ports.in.agents;

import es.vargontoc.pokemon.nuzlocker.domain.models.agents.Situation;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.RuleDecision;

@FunctionalInterface
public interface EncounterCheckUseCase {
    
    RuleDecision evaluate(Situation situation);
}
