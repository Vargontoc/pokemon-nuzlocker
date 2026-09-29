package es.vargontoc.pokemon.nuzlocker.application.ports.out.pokedex;

import tools.jackson.databind.JsonNode;

public interface PokedeLoaderPort {
    
    JsonNode get(String path);

    JsonNode getUrl(String url);
}
