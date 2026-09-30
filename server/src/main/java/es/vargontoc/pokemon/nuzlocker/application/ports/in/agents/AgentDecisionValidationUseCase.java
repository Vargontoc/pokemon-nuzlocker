package es.vargontoc.pokemon.nuzlocker.application.ports.in.agents;

import es.vargontoc.pokemon.nuzlocker.application.dto.RunStateView;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.NuzlockeDecision;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.Situation;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.Validation;

public interface AgentDecisionValidationUseCase {
    
    Validation validate(RunStateView state, Situation situation, NuzlockeDecision decision, EncounterCheckUseCase encounters);
}
