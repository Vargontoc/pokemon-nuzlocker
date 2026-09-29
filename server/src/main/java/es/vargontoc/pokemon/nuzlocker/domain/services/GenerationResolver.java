package es.vargontoc.pokemon.nuzlocker.domain.services;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.IntPredicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.BaseStats;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.GenerationLimits;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.LearnEntry;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.ResolveMove;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.enums.CategoryMoveType;
import tools.jackson.databind.JsonNode;

public class GenerationResolver {
    
    private static final String FIELD_ABILITY = "ability";
    private static final String FIELD_SPECIES = "species";
    private static final String FIELD_PAST_VALUES = "past_values";
    private static final String FIELD_EFFECT_CHANCE = "effect_chance";
    private static final String FIELD_PP = "pp";
    private static final String FIELD_ACCURACY = "accuracy";
    private static final String FIELD_POWER = "power";
    private static final String FIELD_TYPE = "type";
    private static final String VERSION_GROUP = "version_group";
    private static final String FLAVOR_TEXT = "flavor_text";
    private static final String FLAVOR_TEXT_ENTRIES = "flavor_text_entries";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_NAMES = "names";
    private static final String ES = "es";
    private static final String LANGUAGE = "language";

    static final Set<String> PHYSICAL_TYPES = Set.of(
        "normal", "fighting", "flying", "poison", "ground", "rock", "bug", "ghost", "steel"
    );
    static final int LAST_TYPE_BASED_CATEGORY_GEN = 3;
    static final int FIRST_HIDDEN_ABILITY_GEN = 5;


    final Generations generations;
    final int generation;
    final String version;
    final GenerationLimits limits;

    public GenerationResolver(Generations generations, int generation, String versionGroup) {
        this.generations = generations;
        this.generation = generation;
        this.version = versionGroup;
        this.limits = GenerationLimits.of(generation);
    }

    public int generation() {
        return generation;
    }

    public String version() {
        return version;
    }

    public GenerationLimits limits() {
        return limits;
    }

    public static String name(JsonNode resource) {
        for(JsonNode n: resource.path(FIELD_NAMES)){
            if(ES.equals(n.path(LANGUAGE).path(FIELD_NAME).asString()))
                return n.path(FIELD_NAME).asString();
        }
        return resource.path(FIELD_NAME).asString();
    }

    public String description(JsonNode resource) {
        String fallback = null;

        for(JsonNode f :  resource.path(FLAVOR_TEXT_ENTRIES)){
            if(!ES.equals(f.path(LANGUAGE).path(FIELD_NAME).asString()))
                continue;
            String text = f.path(FLAVOR_TEXT).asString().replaceAll("[\\n\\f\\r]", " ").trim();
            if(version.endsWith(f.path(VERSION_GROUP).path(FIELD_NAME).asString()))
                return text;
            fallback = text;
        }
        return fallback;
    }

    @SuppressWarnings("null")
    public List<LearnEntry> learnset(JsonNode pokemon) {
        List<LearnEntry> entries = new ArrayList<>();
        for(JsonNode m : pokemon.path("moves"))  {
            for(JsonNode d: m.path("version_group_details")) {
                if(version.equals(d.path(VERSION_GROUP).path(FIELD_NAME).asString()))
                    entries.add(new LearnEntry(
                        m.path("move").path(FIELD_NAME).asString(),
                        d.path("move_learn_method").path(FIELD_NAME).asString(),
                        d.path("level_learned_at").asInt()
                    ));
            }
        }
        entries.sort(Comparator.comparing(LearnEntry::method).thenComparingInt(LearnEntry::level).thenComparing(LearnEntry::move));
        return entries;
    }

    public List<String> abilities(JsonNode pokemon) {
        if(limits.abilities() == 0) return List.of();

        Map<Integer, JsonNode> bySlot = new TreeMap<>();
        for(JsonNode a : pokemon.path("anilities"))
            bySlot.put(a.path("slot").asInt(), a);

        earliest(pokemon.path("past_abilities"), GenerationResolver::generationOf, g -> g >= generation)
            .ifPresent(entry -> {
                for(JsonNode a : entry.path("abilities")) {
                    int slot = a.path("slot").asInt();
                    if(a.path(FIELD_ABILITY).isNull() || a.path(FIELD_ABILITY).isMissingNode())
                        bySlot.remove(slot);
                    else{
                        bySlot.put(slot, a);
                    }
                }
            });

        return bySlot.values().stream()
            .filter(a -> generation >= FIRST_HIDDEN_ABILITY_GEN || !a.path("is_hidden").asBoolean())
            .filter(a -> idFromUrl(a.path(FIELD_ABILITY).path("url").asString()) <= limits.abilities())
            .map(a -> a.path(FIELD_ABILITY).path(FIELD_NAME).asString()).toList();
    }

    public BaseStats stats(JsonNode pokemon) {
        Map<String, Integer> values = new HashMap<>();
        for(JsonNode s: pokemon.path("stats"))
            values.put(s.path("stat").path(FIELD_NAME).asString(), s.path("base_stat").asInt());

        Set<String> overridden = new HashSet<>();
        for(JsonNode entry : sorted(pokemon.path("past_stats"), GenerationResolver::generationOf, g -> g >= generation)){
            for(JsonNode s: entry.path("stats")){
                String stat = s.path("stat").path(FIELD_NAME).asString();
                if(overridden.add(stat))
                    values.put(stat, s.path("base_stat").asInt());
            }
        }

        return new BaseStats(values.get("hp"), values.get("attack"), values.get("defense"), values.get("special-attack"), values.get("special-defense"), values.get("speed"));
    }

    public Map<String, String> family(JsonNode evolution) {
        JsonNode chain = evolution.path("chain");
        String root = chain.path(FIELD_SPECIES).path(FIELD_NAME).asString();
        Map<String, String> members = new HashMap<>();

        Deque<JsonNode> pending = new ArrayDeque<>(List.of(chain));
        while(!pending.isEmpty()){
            JsonNode node = pending.pop();
            members.put(node.path(FIELD_SPECIES).path(FIELD_NAME).asString(), root);
            node.path("evolves_to").forEach(pending::push);
        }
        return members;
    }

    public List<String> types(JsonNode pokemon) {
        JsonNode source = earliest(pokemon.path("past_types"), GenerationResolver::generationOf, g -> g >= generation)
            .map(e -> e.path("types"))
            .orElse(pokemon.path("types"));
        return stream(source)
            .sorted(Comparator.comparingInt(t -> t.path("slot").asInt()))
            .map(t -> t.path(FIELD_TYPE).path(FIELD_NAME).asString()).toList();
    
    }

    public ResolveMove move(JsonNode move) {
        String type = move.path(FIELD_TYPE).path(FIELD_NAME).asString();
        Integer power = intOtNull(move.path(FIELD_POWER));
        Integer accuracy = intOtNull(move.path(FIELD_ACCURACY));
        Integer pp = intOtNull(move.path(FIELD_PP));
        Integer effectChance = intOtNull(move.path(FIELD_EFFECT_CHANCE));
        int priority = move.path("priortity").asInt();

        Set<String> resolved = new HashSet<>();
        for(JsonNode past : sorted(move.path(FIELD_PAST_VALUES), e -> generations.ofVersion(e.path(VERSION_GROUP).path(FIELD_NAME).asString()), g -> g > generation)) {

            if(hasValue(move, FIELD_POWER) && resolved.add(FIELD_POWER)) power = past.path(FIELD_POWER).asInt();
            if(hasValue(move, FIELD_ACCURACY) && resolved.add(FIELD_ACCURACY)) accuracy = past.path(FIELD_ACCURACY).asInt();
            if(hasValue(move, FIELD_PP) && resolved.add(FIELD_PP)) pp = past.path(FIELD_PP).asInt();
            if(hasValue(move, FIELD_EFFECT_CHANCE) && resolved.add(FIELD_EFFECT_CHANCE)) effectChance = past.path(FIELD_EFFECT_CHANCE).asInt();
            if(hasValue(move, FIELD_TYPE) && resolved.add(FIELD_TYPE)) type = past.path(FIELD_TYPE).path(FIELD_NAME).asString();
        }

        String damageClass = move.path("damage_class").path(FIELD_NAME).asString();
        if(!"status".equals(damageClass) && generation <= LAST_TYPE_BASED_CATEGORY_GEN)
            damageClass = PHYSICAL_TYPES.contains(type) ? "physical" : "special";

        return new ResolveMove(type, power, accuracy, pp, effectChance, CategoryMoveType.valueOf(damageClass), priority);
    }

    static int generationOf(JsonNode entry) {
        return Generations.ofGeneration(entry.path("generation").path(FIELD_NAME).asString());
    }

    static Optional<JsonNode>  earliest(JsonNode array, ToIntFunction<JsonNode> gen, IntPredicate filter){
        return sorted(array, gen, filter).stream().findFirst();
    }

    static List<JsonNode> sorted(JsonNode array, ToIntFunction<JsonNode> gen, IntPredicate filter){
        return stream(array).filter(e -> filter.test(gen.applyAsInt(e))).sorted(Comparator.comparingInt(gen)).toList();
    }

    static Stream<JsonNode> stream(JsonNode array){
        return StreamSupport.stream(array.spliterator(), false);
    }

    static boolean hasValue(JsonNode node, String field){
        return node.has(field) && !node.path(field).isNull();
    }
    static Integer intOtNull(JsonNode node) {
        return node.isNull() || node.isMissingNode() ? null : node.asInt();
    }

    static int idFromUrl(String url){
        String trimmed = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        return Integer.parseInt(trimmed.substring(trimmed.lastIndexOf("/") + 1));
    }
}
