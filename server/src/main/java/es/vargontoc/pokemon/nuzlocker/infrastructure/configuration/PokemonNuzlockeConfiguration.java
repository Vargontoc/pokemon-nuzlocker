package es.vargontoc.pokemon.nuzlocker.infrastructure.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.vargontoc.pokemon.nuzlocker.application.ports.in.SpecieFamilyUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.GameManifestPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.ActiveGame;
import es.vargontoc.pokemon.nuzlocker.domain.services.NuzlockeRulesEngine;

@Configuration
public class PokemonNuzlockeConfiguration {
    
    @Bean
    public ActiveGame activeGame(@Value("${nuzlocke.game}") String value, GameManifestPort manifest) {
        return new ActiveGame(manifest.load(value));
    }

    @Bean 
    public NuzlockeRulesEngine rulesEngine(SpecieFamilyUseCase family) {
        return new NuzlockeRulesEngine(family);
    }
}
