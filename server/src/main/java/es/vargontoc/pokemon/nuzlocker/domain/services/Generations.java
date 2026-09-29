package es.vargontoc.pokemon.nuzlocker.domain.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Generations {
    
    static final Map<String, Integer> ROMAN = Map.of(
        "i", 1,
        "ii", 2,
        "iii", 3,
        "iv", 4,
        "v", 5,
        "vi", 6,
        "vii", 7,
        "viii", 8,
        "ix", 9,
        "x", 10
    );

    final Function<String, Integer> versionGroupLookup;
    final Map<String, Integer> cache = new ConcurrentHashMap<>();

    public Generations(Function<String, Integer> versionGroupLookup){
        this.versionGroupLookup = versionGroupLookup;
    }

    public static int ofGeneration(String name) {
        if(name == null || !name.startsWith("generation-"))
            return -1;
        return ROMAN.getOrDefault(name.substring("generation-".length()), -1);
    }

    public int ofVersion(String version) {
        return cache.computeIfAbsent(version, versionGroupLookup);
    }
}
