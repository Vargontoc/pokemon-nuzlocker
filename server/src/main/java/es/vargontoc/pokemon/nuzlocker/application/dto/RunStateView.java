package es.vargontoc.pokemon.nuzlocker.application.dto;

import java.util.List;
import java.util.Set;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.RunStatusType;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.NuzlockeRules;

public record RunStateView(Long id, String gameId, RunStatusType status, NuzlockeRules rules,  String starter, CurrentStep currentStep, String locationId, Set<String> badges, Set<String> flags, Set<String> unlocks, Integer levelCap, List<PokemonView> party, List<PokemonView> box, List<PokemonView> graveyard, List<String>  usedZones) {
    public record  CurrentStep(String id, int order, String objective) {
    }
}
