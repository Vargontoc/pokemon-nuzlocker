package es.vargontoc.pokemon.nuzlocker.domain.models.agents;

import java.util.List;

/** Resultado de un turno. Si validation.valid es false tras agotar los intentos la decisión NO debe ejecutarse */
public record TurnResult(String stepId, GuideBriefing guide, NuzlockeDecision decision, Validation validation, int attempts, List<String> errors, long elapsedMs) {
    
}
