package es.vargontoc.pokemon.nuzlocker.application.ports.out.persitence;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.EncounterRecord;

public interface RunEncounterPort {

    EncounterRecord save(EncounterRecord entity);
}
