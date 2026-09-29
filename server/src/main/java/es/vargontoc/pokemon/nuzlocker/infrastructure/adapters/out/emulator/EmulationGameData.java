package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.GuideCatalogPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.ActiveGame;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class EmulationGameData {
    
    private final Map<String, String> maps = new LinkedHashMap<>();
    private final Map<String, Integer> badges = new LinkedHashMap<>();
    private final Map<String, Integer> flags = new LinkedHashMap<>();

    public EmulationGameData(ObjectMapper mapper, GuideCatalogPort catalog) {
        String base = ActiveGame.path(catalog.gameId(), "emulator/");
        readJson(mapper, base + "map-locations.json").path("maps").properties()
                .forEach(e -> maps.put(e.getKey(), e.getValue().asString()));
        JsonNode gameFlags = readJson(mapper, base + "game-flags.json");
        gameFlags.path("badges").properties().forEach(e -> badges.put(e.getKey(), Integer.decode(e.getValue().asString())));
        gameFlags.path("flags").properties().forEach(e -> flags.put(e.getKey(), Integer.decode(e.getValue().asString())));

        maps.values().forEach(locationId -> catalog.location(locationId).orElseThrow(() ->
                new IllegalStateException("map-locations.json usa una localización inexistente: " + locationId)));
    }

    public Optional<String> locationFor(int mapGroup, int mapNum) {
        return Optional.ofNullable(maps.get(mapGroup + "." + mapNum));
    }

    public Map<String, Integer> badges() {
        return Collections.unmodifiableMap(badges);
    }

    public Map<String, Integer> flags() {
        return Collections.unmodifiableMap(flags);
    }

    static JsonNode readJson(ObjectMapper mapper, String path) {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            return mapper.readTree(in);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + path, e);
        }
    }

        /** Primer mapa (grupo, número) asociado a una localización. */
    public Optional<int[]> mapFor(String locationId) {
        return maps.entrySet().stream()
                .filter(e -> e.getValue().equals(locationId))
                .findFirst()
                .map(e -> {
                    String[] parts = e.getKey().split("\\.");
                    return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
                });
    }

}
