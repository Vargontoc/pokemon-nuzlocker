package es.vargontoc.pokemon.nuzlocker.application.ports.in.agents;

import es.vargontoc.framework.exception.AgentOutputException;
import es.vargontoc.pokemon.nuzlocker.application.dto.RunStateView;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.GuideBriefing;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.GuidePackage;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.NuzlockeDecision;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.Situation;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.TurnResult;

/** Turno del agente */
public interface AgentTurnUseCase {
    TurnResult play(Long runId, Situation situation);

    NuzlockeDecision decide(Long runId, RunStateView state, Situation situation, GuidePackage guide, String rejection) throws AgentOutputException;

    GuideBriefing brief(int currentOrder, String question) throws AgentOutputException;
}
