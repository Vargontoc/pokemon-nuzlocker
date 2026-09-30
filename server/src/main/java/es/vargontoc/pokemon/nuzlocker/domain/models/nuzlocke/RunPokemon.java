package es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.RunPokemonStatusType;

public record RunPokemon(Long id, long runId, String specie, String nickname, int level, RunPokemonStatusType status, boolean shiny, String locationId, int caughtAtStep, String causeofDeath, String identityKey) {
    public RunPokemon identity(String identity) {
        return new RunPokemon(id, runId, specie, nickname, level, status, shiny, locationId, caughtAtStep, causeofDeath, identity);
    }

    public String displayName() {
        return nickname != null ? nickname : specie;

    }

    public RunPokemon levelUp(int level) {
        return new RunPokemon(id, runId, specie, nickname, level, status, shiny, locationId, caughtAtStep, causeofDeath, identityKey);
    }

    public RunPokemon die(String causeOfDeath) {
        return new RunPokemon(id, runId, specie, nickname, level, status, shiny, locationId, caughtAtStep, causeOfDeath, identityKey);
    }

    public boolean isAlive() { return status != RunPokemonStatusType.DEATH; }
}
