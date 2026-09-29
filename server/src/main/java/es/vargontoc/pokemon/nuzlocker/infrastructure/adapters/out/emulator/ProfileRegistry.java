package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.domain.models.ActiveGame;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.RomId;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.profile.GameProfile;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.profile.SymbolExpression;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProfileRegistry {
    
    final ActiveGame game;
    final Map<String, GameProfile> profiles = new TreeMap<>();

    public ProfileRegistry(ObjectMapper mapper, ActiveGame game) {
        this.game = game;
        String pattern = "classpath*:" + ActiveGame.path(game.id(), "profiles/*.json");
        try {
            for (Resource resource : new PathMatchingResourcePatternResolver().getResources(pattern)) {
                try (InputStream in = resource.getInputStream()) {
                    GameProfile profile = parse(mapper.readTree(in));
                    profiles.put(profile.key(), profile);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudieron leer los perfiles de " + game.id(), e);
        }
    }


        /**
     * @throws IllegalStateException si la ROM no es de este juego o no hay perfil para su versión
     */
    public GameProfile require(RomId rom) {
        if (!game.manifest().romCodes().contains(rom.gameCode())) {
            throw new IllegalStateException("La ROM %s no es de %s (códigos admitidos: %s). ¿Está bien nuzlocke.game?"
                    .formatted(rom.gameCode(), game.manifest().name(), game.manifest().romCodes()));
        }
        GameProfile profile = profiles.get(rom.key());
        if (profile == null) {
            throw new IllegalStateException("No hay perfil de memoria para %s. Perfiles disponibles: %s. Añade %s"
                    .formatted(rom.key(), profiles.keySet(), ActiveGame.path(game.id(), "profiles/" + rom.key() + ".json")));
        }
        return profile;
    }

    public Set<String> available() {
        return Collections.unmodifiableSet(profiles.keySet());
    }

    static GameProfile parse(JsonNode n) {
        Map<String, SymbolExpression> symbols = new LinkedHashMap<>();
        n.path("symbols").properties().forEach(e -> symbols.put(e.getKey(), SymbolExpression.parse(e.getValue().asString())));
        Map<String, Integer> sizes = new LinkedHashMap<>();
        n.path("sizes").properties().forEach(e -> sizes.put(e.getKey(), Integer.decode(e.getValue().asString())));
        JsonNode range = n.path("pointerRange");
        Long min = range.size() == 2 ? Long.decode(range.get(0).asString()) : null;
        Long max = range.size() == 2 ? Long.decode(range.get(1).asString()) : null;
        return new GameProfile(n.path("romCode").asString(), n.path("revision").asInt(), n.path("description").asString(),
                n.path("pointerSize").asInt(4), min, max, Map.copyOf(symbols), Map.copyOf(sizes));
    }
}
