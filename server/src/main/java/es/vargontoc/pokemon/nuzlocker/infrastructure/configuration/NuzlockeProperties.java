package es.vargontoc.pokemon.nuzlocker.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nuzlocke")
public record NuzlockeProperties(String game, PokeApiProperties pokeApi) {
    
}
