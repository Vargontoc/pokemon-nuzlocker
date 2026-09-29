package es.vargontoc.pokemon.nuzlocker.infrastructure.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.vargontoc.pokemon.nuzlocker.application.ports.in.SpecieFamilyUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.GameManifestPort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.pokedex.PokedexLoaderPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.ActiveGame;
import es.vargontoc.pokemon.nuzlocker.domain.services.GenerationResolver;
import es.vargontoc.pokemon.nuzlocker.domain.services.Generations;
import es.vargontoc.pokemon.nuzlocker.domain.services.NuzlockeRulesEngine;

@Configuration
public class PokemonNuzlockeConfiguration {
    
    @Bean
    public ActiveGame activeGame(NuzlockeProperties props, GameManifestPort manifest) {
        return new ActiveGame(manifest.load(props.game()));
    }

    @Bean
    public NuzlockeRulesEngine rulesEngine(SpecieFamilyUseCase family) {
        return new NuzlockeRulesEngine(family);
    }

    @Bean
    public Generations generations(PokedexLoaderPort port) {
        return new Generations(vg -> Generations.ofGeneration(port.get("version-group/" + vg).path("generation").path("name").asString()));
    }

    @Bean
    public GenerationResolver generationResolver(Generations generations, ActiveGame activeGame) {
        return new GenerationResolver(generations, activeGame.manifest().generation(), activeGame.manifest().version());
    }
}
