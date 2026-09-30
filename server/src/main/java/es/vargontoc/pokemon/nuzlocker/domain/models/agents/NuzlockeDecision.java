package es.vargontoc.pokemon.nuzlocker.domain.models.agents;

import java.util.List;

import es.vargontoc.pokemon.nuzlocker.domain.models.agents.enums.DecisionActionType;

/** Decisión que toma el agente */
public record NuzlockeDecision(DecisionActionType action, String target, String nickname, String reasoning, List<String> risks) {
    
}
