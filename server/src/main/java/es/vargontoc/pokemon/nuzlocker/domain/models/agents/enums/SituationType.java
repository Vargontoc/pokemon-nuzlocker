package es.vargontoc.pokemon.nuzlocker.domain.models.agents.enums;

import java.util.Set;

import static es.vargontoc.pokemon.nuzlocker.domain.models.agents.enums.DecisionActionType.*;
public enum SituationType {
    STARTED_CHOICE(Set.of(CHOOSE_STARTER)),
    WILD_ENCOUNTER(Set.of(CATCH, DONT_CATCH)),
    TRAINER_AHEAD(Set.of(FIGHT, AVOID, TRAIN)),
    NEXT_STEP(Set.of(PROCEED, TRAIN));

    final Set<DecisionActionType> allowedAction;

    SituationType(Set<DecisionActionType> allowedActions){
        this.allowedAction = allowedActions;
    }

    public Set<DecisionActionType> allowed() {
        return allowedAction;
    }
}
