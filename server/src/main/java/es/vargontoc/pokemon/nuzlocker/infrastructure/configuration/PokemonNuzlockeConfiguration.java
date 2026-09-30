package es.vargontoc.pokemon.nuzlocker.infrastructure.configuration;


import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.vargontoc.framework.ai.client.springai.AgentChatClientCustomizer;
import es.vargontoc.framework.ai.client.springai.AgentChatClients;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.SpecieFamilyUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.GameManifestPort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.pokedex.PokedexLoaderPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.ActiveGame;
import es.vargontoc.pokemon.nuzlocker.domain.services.GenerationResolver;
import es.vargontoc.pokemon.nuzlocker.domain.services.Generations;
import es.vargontoc.pokemon.nuzlocker.domain.services.NuzlockeRulesEngine;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.tools.GuideTools;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.tools.NuzlockeTools;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.tools.PokedexTools;

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

    @Bean("guide-agent-custom")
    public AgentChatClientCustomizer guideAgent(GuideTools tools) {
        return (dominio, agente, builder) -> {
            if(dominio.equals("guide-agent"))
                builder
                .defaultTools(List.of(tools))
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor()));
        };
    }

    @Bean("nuzlocke-agent-custom")
    public AgentChatClientCustomizer nuzlockeAgent(PokedexTools pokedex, NuzlockeTools nuzlocke) {
        return (dominio, agente, builder) -> {
            if(dominio.equals("guide-agent"))
                builder
                .defaultTools(List.of(pokedex, nuzlocke))
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor()));
        };
    }

    @Bean("guide-chat")
    public ChatClient chatGuide(AgentChatClients clients){
        return clients.find("guide-agent").orElseThrow();
    }

    @Bean("nuzlocke-chat")
    public ChatClient chatNuzlocke(AgentChatClients clients) {
        return clients.find("guide-agent").orElseThrow();
    }
}
