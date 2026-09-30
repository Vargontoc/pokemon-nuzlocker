package es.vargontoc.pokemon.nuzlocker.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;


import es.vargontoc.framework.ai.domain.PromptJson;
import es.vargontoc.framework.ai.domain.StructuredOutput;
import es.vargontoc.framework.exception.AgentOutputException;
import es.vargontoc.pokemon.nuzlocker.application.dto.RunStateView;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.NuzlockeUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.agents.AgentDecisionValidationUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.agents.AgentTurnUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.GuideCatalogPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.GuideBriefing;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.GuidePackage;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.NuzlockeDecision;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.Situation;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.TurnResult;
import es.vargontoc.pokemon.nuzlocker.domain.models.agents.Validation;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.StepContext;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.EncounterRecord;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.RunStatusType;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.tools.NuzlockeTools;

@Service
public class AgentTurnService implements AgentTurnUseCase {

    @Value("classpath:prompts/guide-question.st")
    Resource promptGuide;

    @Value("classpath:prompts/nuzlocke-action.st")
    Resource promptNuzlocke;

    static Logger log = LoggerFactory.getLogger(AgentTurnService.class);
    static final int UPCOMING_STEPS = 2;
    static final int MAX_ATTEMPTS = 3;

    final NuzlockeUseCase runs;
    final GuideCatalogPort catalog;
    final AgentDecisionValidationUseCase validator;

    final GuideContextService context;
    final ChatClient nuzlocke;
    final ChatClient guide;

    final PromptJson json;
    public AgentTurnService(
        PromptJson prompter,
        @Qualifier("nuzlocke-chat") ChatClient guide,
        @Qualifier("guide-chat") ChatClient nuzlocke,
        NuzlockeUseCase runs, GuideContextService context,  GuideCatalogPort catalog, AgentDecisionValidationUseCase validator) {
        this.runs = runs;
        this.catalog = catalog;
        this.validator = validator;
        this.context = context;
        this.guide = guide;
        this.nuzlocke = nuzlocke;
        this.json = prompter;
    }



    @Override
    public TurnResult play(Long runId, Situation situation) {
        long start = System.currentTimeMillis();
        situation.validate();

        RunStateView state = runs.state(runId);
        if(state.status() != RunStatusType.ACTIVE)
            throw new IllegalStateException("La partida ya ha terminado: " + state.status());

        List<String> errors = new ArrayList<>();
        int order = state.currentStep().order();

        GuidePackage guide = new GuidePackage(context.contextFor(order).orElseThrow(), upcoming(order), briefing(order, situation, errors));
    
        NuzlockeDecision decision = null;
        Validation validation = Validation.reject("Sin decisión");
        String rejection = null;
        int attempt = 0;

        while(attempt < MAX_ATTEMPTS) {
            attempt++;
            try {
                decision = decide(runId, state, situation, guide, rejection);
                validation = validator.validate(state, situation, decision, s -> runs.evaluateEncounter(runId, 
                    new EncounterRecord(runId, s.location(), s.specie(), s.shiny(), null, false, order)
                ));
            }catch(AgentOutputException e) {
                log.warn("poke-nuzlocke devolvió una respuesta ilegible: " + e.getMessage());
                validation = Validation.reject("Respuesta ilegible: " + e.getMessage());
            }

            if(validation.valid())
                break;

            errors.add("Intento %d rechazado: %s".formatted(attempt, validation.reason()));
            rejection = validation.reason();;
        }

        return new TurnResult(state.currentStep().id(), guide.briefing(), decision, validation, attempt, errors, System.currentTimeMillis() -start);
    }

    @SuppressWarnings("null")
    List<StepContext> upcoming(int order) {
        return IntStream.rangeClosed(order + 1, order + UPCOMING_STEPS).mapToObj(context::contextFor).flatMap(Optional::stream).toList();
    }



    @Override
    public NuzlockeDecision decide(Long runId, RunStateView state, Situation situation, GuidePackage guide,
        String rejection) throws AgentOutputException {
        
            String feedback = rejection == null ? ""
                : "IMPORTANTE: tu decisión anterior fue rechazada por el motor de reglas: " + rejection + ". Elige otra acción.";
        var converter = new BeanOutputConverter<>(NuzlockeDecision.class);
        String raw = nuzlocke.prompt().user(u -> u.text(promptNuzlocke)
            .param("rules", json.write(state.rules()))
            .param("state", json.write(state))
            .param("situation", json.write(situation))
            .param("actions", situation.type().allowed().toString())
            .param("guide", json.write(guide))
            .param("feedback", feedback)
            .param("format", converter.getFormat())
        )
        .toolContext(Map.of(NuzlockeTools.RUN_ID, runId))
        .call().content();
        return StructuredOutput.parse(raw, converter);
    }

    @Override
    public GuideBriefing brief(int currentOrder, String question) throws AgentOutputException {
        var converter = new BeanOutputConverter<>(GuideBriefing.class);
        String raw = guide.prompt().user(u -> u.text(promptGuide)
            .param("order", currentOrder)
            .param("question", question)
            .param("format", converter.getFormat())
        ).call().content();
        return StructuredOutput.parse(raw, converter);
    }
    private GuideBriefing briefing(int order, Situation situation, List<String> errors){
        try {
            return brief(order, situation.guideQuestion());
        }catch(AgentOutputException e) {
            log.warn("guide-agent devolvió una respuesta ilegible: {}", e.getRaw());
            errors.add("guide-agent: " + e.getMessage());
            return null;
        }
    }

}
