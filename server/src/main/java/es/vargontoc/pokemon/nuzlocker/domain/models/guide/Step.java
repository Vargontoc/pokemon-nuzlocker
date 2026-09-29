package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import java.util.List;
import java.util.Map;


public record Step(String id, int order, String locationId, Precondition preconditions, String objective, String text, List<String> trainers, List<Map<String, Object>> rewards, List<String> unlocks, CompletionCondition completeWhen, String next) {    

    public boolean isIndexable(){
        return text != null && !text.isBlank();
    }
}
