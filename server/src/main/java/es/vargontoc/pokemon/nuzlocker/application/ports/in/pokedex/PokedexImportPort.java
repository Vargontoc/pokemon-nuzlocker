package es.vargontoc.pokemon.nuzlocker.application.ports.in.pokedex;

import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.ImportSummary;

public interface PokedexImportPort {

    boolean isComplete();
    
    ImportSummary importAll();
}
