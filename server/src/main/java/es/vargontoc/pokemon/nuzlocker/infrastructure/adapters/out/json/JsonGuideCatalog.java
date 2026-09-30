package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.GuideCatalogPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.ActiveGame;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Connection;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Location;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Step;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Trainer;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.TrainerPokemon;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Component
public class JsonGuideCatalog implements GuideCatalogPort {

    private static final Logger log = LoggerFactory.getLogger(JsonGuideCatalog.class);

    private final String gameId;
    private final String basePath;

    private final Map<String, Location> locations;
    private final Map<String, Trainer> trainers;
    private final Map<String, Step> stepsById;

    private final List<Step> steps;
    private final List<String> warnings = new ArrayList<>();

    public JsonGuideCatalog(ActiveGame game) {
        this.gameId = game.id();
        this.basePath = ActiveGame.path(gameId, "guide") + "/";

        ObjectMapper mapper = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();

        this.locations = indexById(Arrays.asList(read(mapper, basePath + "locations.json", Location[].class)),
                l -> l.id(),
                "localización");
        this.trainers = indexById(Arrays.asList(read(mapper, basePath  +  "trainers.json", Trainer[].class)),
                l -> l.id(),
                "entrenador");

        this.steps = Arrays.asList(read(mapper, basePath + "steps.json", Step[].class)).stream()
                .sorted(Comparator.comparingInt(s -> s.order())).toList();
        this.stepsById = indexById(steps, s -> s.id(), "paso");

        validate();
        
    }

    /**
     * Valida que la guía es correcta
     */
    private void validate() {
        List<String> errors = new ArrayList<>();

        // Validar localizaciones
        for (Location loc : locations.values()) {
            if (loc.parentId() != null && !locations.containsKey(loc.parentId()))
                errors.add("Localización %s: parentId inexistente %s".formatted(loc.id(), loc.parentId()));

            if (loc.gym() != null && !trainers.containsKey(loc.gym().leaderTrainerId()))
                errors.add("Localización %s: lider inexistente %s".formatted(loc.id(), loc.gym().leaderTrainerId()));

            for (Connection c : loc.connections()) {
                if (!locations.containsKey(c.to())) {
                    warnings.add("Localización %s se conecta con %s, que aún no existe".formatted(loc.id(), c.to()));
                }
            }
        }

        // Validar entrenadores
        for (Trainer t : trainers.values()) {
            if (!locations.containsKey(t.locationId()))
                errors.add("Entrenador %s: localización inexistente".formatted(t.id(), t.locationId()));

            for (TrainerPokemon tp : t.team()) {
                if (tp.specie() == null)
                    errors.add("Entrenador %s: cada Pokémon necesita specie".formatted(t.id()));
            }
        }

        // Validar pasos
        Set<String> trainersInSteps = new HashSet<>();
        int previousOrder = Integer.MIN_VALUE;
        for (Step s : steps) {
            if (s.order() == previousOrder)
                errors.add("Order duplicado: " + s.order());
            previousOrder = s.order();

            if (s.next() != null && !stepsById.containsKey(s.next()))
                errors.add("Paso %s: next inexistente %s".formatted(s.id(), s.next()));

            for (String t : s.trainers()) {
                if (!trainers.containsKey(t))
                    errors.add("Paso %s: entrenador inexistente %s".formatted(s.id(), t));
                trainersInSteps.add(t);
            }

        }
        trainers.keySet().stream().filter(t -> !trainersInSteps.contains(t))
                .forEach(t -> warnings.add("Entrenador %s no está asignado a ningún paso".formatted(t)));
        if (!errors.isEmpty())
            throw new IllegalStateException("Guía inválida: |n -" + String.join("\n -", errors));

        log.info("Guía de {} cargada: {} localizaciones, {} entrenadores, {} pasos", gameId, locations.size(),
                trainers.size(), steps.size());
        warnings.forEach(w -> log.warn("Guía: {}", w));
    }


    @Override
    public String gameId() {
        return gameId;
    }

    @Override
    public Optional<Location> location(String id) {
        return Optional.ofNullable(locations.get(id));
    }

    @Override
    public Collection<Location> locations() {
        return locations.values();
    }

    @Override
    public Optional<Trainer> trainer(String id) {
        return Optional.ofNullable(trainers.get(id));
    }

    @Override
    public Collection<Trainer> trainers() {
        return trainers.values();
    }
    @Override
    public Optional<Step> step(String id) {
        return Optional.ofNullable(stepsById.get(id));
    }

    @Override
    public Optional<Step> stepByOrder(int order) {
        return steps.stream().filter(s -> s.order() == order).findFirst();
    }

    @Override
    public List<Step> steps() {
        return steps;
    }

    @Override
    public List<String> warnings() {
        return warnings;
    }


        private static <T> T read(ObjectMapper mapper, String path, Class<T> type) {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            return mapper.readValue(in, type);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + path, e);
        }
    }

    private static <T> Map<String, T> indexById(List<T> items, Function<T, String> id, String kind) {
        Map<String, T> index = new LinkedHashMap<>();
        for (T item : items) {
            if (index.putIfAbsent(id.apply(item), item) != null) {
                throw new IllegalStateException(String.format("Id de %s duplicado: %s", kind, id.apply(item)));
            }
        }
        return Collections.unmodifiableMap(index);
    }
    
}
