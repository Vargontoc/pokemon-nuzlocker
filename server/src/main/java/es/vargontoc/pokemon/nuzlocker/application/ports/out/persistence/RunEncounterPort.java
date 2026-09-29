package es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.EncounterRecord;

public interface RunEncounterPort {

    EncounterRecord save(EncounterRecord entity);
}
