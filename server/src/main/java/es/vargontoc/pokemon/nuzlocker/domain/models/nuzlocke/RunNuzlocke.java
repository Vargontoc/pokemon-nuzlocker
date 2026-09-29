package es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.RunStatusType;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.NuzlockeRules;

public record RunNuzlocke(Long id, String gameId, RunStatusType status, String starter, int currentStepOrder, String currentLocationId, Set<String> badges, Set<String> flags, Set<String> defeatedTrainers, Set<String> unlocks, NuzlockeRules rules) {
    
    public static RunNuzlocke init(String gameId, NuzlockeRules rules, int order, String location) {
        return new RunNuzlocke(null, gameId, RunStatusType.ACTIVE, null, order, location, Set.of(), Set.of(), Set.of(), Set.of(), rules);
    }

    public RunNuzlocke starter(String specie) {
        if(starter != null)
            throw new IllegalStateException("El inicial ya fue elegido: " + starter);

        return new RunNuzlocke(id, gameId, status, specie, currentStepOrder, currentLocationId, badges, flags, defeatedTrainers, unlocks, rules);
    }

    public RunNuzlocke addBadge(String value) {
        return new RunNuzlocke(id, gameId, status, starter, currentStepOrder, currentLocationId, with(badges, value), flags, defeatedTrainers, unlocks,rules);
    }

    public RunNuzlocke addFlag(String value) {
        return new RunNuzlocke(id, gameId, status, starter, currentStepOrder, currentLocationId, badges, with(flags, value), defeatedTrainers, unlocks, rules);
    }

    public RunNuzlocke addUnlocks(Collection<String> values) {
        Set<String> added = new HashSet<>();
        Set<String> tmp = new LinkedHashSet<>(unlocks);
        values.forEach(v -> added.addAll(with(tmp, v)));
        return new RunNuzlocke(id, gameId, status, starter, currentStepOrder, currentLocationId, badges, flags, defeatedTrainers, added, rules);
    }

    public RunNuzlocke addDefeatedTrainer(String value) {
        return new RunNuzlocke(id, gameId, status, starter, currentStepOrder, currentLocationId, badges, flags, with(defeatedTrainers, value), unlocks, rules);
    }

    public RunNuzlocke goToStep(int step) {
        return new RunNuzlocke(id, gameId, status, starter, step, currentLocationId, badges, flags, defeatedTrainers, unlocks, rules);
    }

    public RunNuzlocke moveTo(String location) {
        return new RunNuzlocke(id, gameId, status, starter, currentStepOrder, location, badges, flags, defeatedTrainers, unlocks, rules);

    }

    public RunNuzlocke lose(){
        return new RunNuzlocke(id, gameId, RunStatusType.LOST, starter, currentStepOrder, currentLocationId, badges, flags, defeatedTrainers, unlocks, rules);
    }

    public boolean isCatchingUnlocked() {
        return unlocks.contains("CATCHING");
    }

    static Set<String> with(Set<String> current, String value) {
        Set<String> copy = new LinkedHashSet<>(current);
        copy.add(value);
        return copy;
    }
}
