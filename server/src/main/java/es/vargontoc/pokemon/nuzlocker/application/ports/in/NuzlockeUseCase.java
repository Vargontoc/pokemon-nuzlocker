package es.vargontoc.pokemon.nuzlocker.application.ports.in;

import es.vargontoc.pokemon.nuzlocker.application.dto.EventResult;
import es.vargontoc.pokemon.nuzlocker.application.dto.RunStateView;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.CompletionCondition;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.EncounterRecord;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunPokemon;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.NuzlockeRules;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.RuleDecision;

public interface NuzlockeUseCase {

    RunStateView create(NuzlockeRules rules);

    RunStateView state(Long id);

    EventResult chooseStarter(Long id, String specie, String nickname);

    EventResult applyEvent(Long id, CompletionCondition condition);

    RuleDecision evaluateEncounter(Long runId, EncounterRecord request);

    RunStateView recordEncounter(Long id, RunPokemon encounter, boolean caught);

    RunStateView faint(Long runId, Long pokemonId, String causeOfDeath);

    RunStateView updateLevel(Long runId, Long pokemonId, int level);

    void linkIdentity(Long runId, Long pokemonId, String identityKey);

    RuleDecision evaluateLevel(Long runId, int level);
}
