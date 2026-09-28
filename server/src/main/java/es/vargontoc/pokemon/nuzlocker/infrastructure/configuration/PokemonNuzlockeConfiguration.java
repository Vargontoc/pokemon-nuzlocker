package es.vargontoc.pokemon.nuzlocker.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.vargontoc.pokemon.nuzlocker.domain.models.ActiveGame;

@Configuration
public class PokemonNuzlockeConfiguration {
    
    @Bean
    public ActiveGame activeGame(@Value("${nuzlocke.game}") String gameId) {
        return new ActiveGame(gameId);
    }
}
