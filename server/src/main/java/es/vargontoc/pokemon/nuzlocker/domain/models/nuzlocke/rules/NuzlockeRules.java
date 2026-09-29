package es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules;

import java.util.LinkedHashSet;
import java.util.Set;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.LevelCapModeType;

public record NuzlockeRules(boolean dupesClause, boolean shinyClause, LevelCapModeType levelCap, boolean battleItemsAllowed, boolean setBattleStyle) {
    
    public NuzlockeRules {
        if(levelCap == null)
            levelCap = LevelCapModeType.NONE;
    }

    public Set<String> enabledOptionalRules() {
        Set<String> enabled = new LinkedHashSet<>();
        if(dupesClause) enabled.add("dupesClause");
        if(shinyClause) enabled.add("shinyClause");
        if(levelCap != LevelCapModeType.NONE) enabled.add("levelCap");
        if(!battleItemsAllowed) enabled.add("noBattleItems");
        if(setBattleStyle) enabled.add("setBattleStyle");
        return enabled;
    }

    public static NuzlockeRules standard() {
        return new NuzlockeRules(true, true, LevelCapModeType.NEXT_GYM_LEADER, true, true);
    }
}
