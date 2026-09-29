package es.vargontoc.pokemon.nuzlocker.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nuzlocke.pokeapi")
public record PokeApiProperties(String baseUrl, long requestDelayMs) {
}
