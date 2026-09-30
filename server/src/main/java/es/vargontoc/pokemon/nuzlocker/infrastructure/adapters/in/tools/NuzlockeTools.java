package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.tools;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.ports.in.NuzlockeUseCase;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.RuleDecision;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.web.dto.EncounterRequest;

@Component
public class NuzlockeTools {
    public static final String RUN_ID = "runId";
    final NuzlockeUseCase nuzlocke;

    public NuzlockeTools(NuzlockeUseCase nuzlocke) {
        this.nuzlocke = nuzlocke;
    }

    @Tool(name = "evaluate_encounter", description = "Comprueba si capturar este Pokémon salvaje es legal según las reglas Nuzlocke de la partida")
    public RuleDecision evaluateEncounter(
        @ToolParam(description = "id de la localización") String locationId,
        @ToolParam(description = "slug de la especie") String specie,
        @ToolParam(description = "si es shiny") boolean shiny,
        ToolContext ctx
    ) {
        return nuzlocke.evaluateEncounter(runId(ctx), new EncounterRequest(locationId, specie, shiny, 0, null, false).to());
    }

    @Tool(name = "check_level", description = "Comprueba si un nivel respeta el límite de nivel de la partida")
    public RuleDecision checkLevel(@ToolParam(description = "nivel a comprobar") int level, ToolContext ctx) {
        return nuzlocke.evaluateLevel(runId(ctx), level);
    }

    static Long runId(ToolContext ctx){
        return (long) ctx.getContext().get(RUN_ID);
    }
}
