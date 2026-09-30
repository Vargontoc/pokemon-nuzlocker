package es.vargontoc.pokemon.nuzlocker.domain.models.agents;

import java.util.List;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.StepContext;

/** Paquete de guía para agente poke-nuzlocke: datos exactos del backend + resumen del guide-agent (puede ser null). */
public record GuidePackage(StepContext current, List<StepContext> upcoming, GuideBriefing briefing) {
    
}
