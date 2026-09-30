package es.vargontoc.pokemon.nuzlocker.application.services;

import java.util.Optional;

import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.EncounterResolution;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.GameSnapshot;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PendingEncounter;

@Component
public class EncounterTracker {
    
    private PendingEncounter pending;

    public void start(PendingEncounter encounter) {
        this.pending = encounter;
    }

    public Optional<PendingEncounter> pending() {
        return Optional.ofNullable(pending);
    }

    public Optional<EncounterResolution> observe(GameSnapshot snapshot, String currentLocationId) {
        if(pending == null)
            return Optional.empty();

        boolean caught = snapshot.party().stream().anyMatch(m -> m.valid() && m.identityKey().equals(pending.identityKey()));
        boolean newBattle = snapshot.enemyLead() != null && snapshot.enemyLead().valid() && !snapshot.enemyLead().identityKey().equals(pending.identityKey());
        boolean leftZone = currentLocationId != null && !currentLocationId.equals(pending.locationId());

        if(caught || newBattle || leftZone) {
            EncounterResolution resolution = new EncounterResolution(pending, caught);
            pending = null;
            return Optional.of(resolution);
        }

        return Optional.empty();
    }
}
