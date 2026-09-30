package es.vargontoc.pokemon.nuzlocker.domain.models.agents;

import java.util.List;

/** La respuesta del guide-agent: interpretación en lenguaje natural
 * Los datos exactos (equipos, niveles, encuentros) no pasan por el modelo
 */
public record GuideBriefing(String answer, String guideNotes, List<String> warnings, List<String> sources) {
    
}
