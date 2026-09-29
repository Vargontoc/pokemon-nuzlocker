package es.vargontoc.pokemon.nuzlocker.application.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import es.vargontoc.pokemon.nuzlocker.application.ports.in.pokedex.PokedexImportPort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.AbilityPort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.MovePort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.SpeciePort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.pokedex.PokedexLoaderPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Ability;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.GenerationLimits;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.ImportSummary;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Move;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Specie;
import es.vargontoc.pokemon.nuzlocker.domain.services.GenerationResolver;
import tools.jackson.databind.JsonNode;

@Service
public class PokedexImporter implements PokedexImportPort {

    static final Logger log = LoggerFactory.getLogger(PokedexImporter.class);
    
    final PokedexLoaderPort client;
    final GenerationResolver resolver;

    final SpeciePort species;
    final MovePort moves;
    final AbilityPort abilities;

    final JdbcTemplate jdbc;

    

    public PokedexImporter(PokedexLoaderPort client, GenerationResolver resolver, SpeciePort species, MovePort moves,
            AbilityPort abilities, JdbcTemplate jdbc) {
        this.client = client;
        this.resolver = resolver;
        this.species = species;
        this.moves = moves;
        this.abilities = abilities;
        this.jdbc = jdbc;
    }



    @Override
    public ImportSummary importAll() {
        GenerationLimits limits = resolver.limits();
        log.info("Importanto Pokédex de Gen {} ({})", resolver.generation(), resolver.version());

        importMoves(limits.moves());
        importSpecies(limits.species());
        importAbilities(limits.abilities());
        
        species.deleteAbove(limits.species());
        moves.deleteAbove(limits.moves());
        abilities.deleteAbove(limits.abilities());

        jdbc.update("""
            insert into pokedex_meta (id, generation, version_group) values (1, ?, ?)
            on conflict (id) do update set generation = excluded.generation, version_group = excluded.version_group
        """, resolver.generation(), resolver.version());

        return new ImportSummary(resolver.generation(), resolver.version(), species.count(), moves.count(), abilities.count());
    }



    @Override
    public boolean isComplete() {
        GenerationLimits limits = resolver.limits();

        List<String> imported = jdbc.queryForList("select version_group from pokedex_meta where id = 1", String.class);
        return imported.contains(resolver.version()) &&
            species.count() == limits.species() &&
            moves.count() == limits.moves() &&
            abilities.count() == limits.abilities();
    }
    
    void importSpecies(int total) {
        Map<String, String> families = new HashMap<>();
        for(int id = 1; id <= total; id++) {
            JsonNode pokemon = client.get("pokemon/" + id);
            JsonNode specie = client.get("pokemon-species/" + id);

            String slug = pokemon.path("species").path("name").asString();
            if(!families.containsKey(slug))
                families.putAll(resolver.family(client.getUrl(specie.path("evolution_chain").path("url").asString())));

            species.save(new Specie(Long.valueOf(id), slug, GenerationResolver.name(specie), resolver.types(pokemon), resolver.stats(pokemon), resolver.abilities(pokemon), families.getOrDefault(slug, slug), resolver.learnset(pokemon)));

            logProgress("Especies", id, total);
        }
    }

    void importMoves(int total) {
        for(int id = 1; id <= total; id++){
            JsonNode move = client.get("move/" + id);
            var r = resolver.move(move);

            moves.save(new Move(Long.valueOf(id), move.path("name").asString(), GenerationResolver.name(move), resolver.description(move), r.type(), r.power(), r.accuracy(), r.pp(), r.effectChance(), r.category(), r.priority()));
        
            logProgress("Movimientos", id, total);
        }
    }

    void importAbilities(int total) {
        for(int id = 1; id <= total; id++){
            JsonNode ability = client.get("ability/" + id);
            abilities.save(new Ability(Long.valueOf(id), ability.path("name").asString(), GenerationResolver.name(ability), resolver.description(ability)));
        
            logProgress("Habilidades", id, total);
        }
    }

    static void logProgress(String what, int done, int total) {
        if(done % 50 == 0 || done == total)
            log.info("{}: {}/{}", what, done, total);
    }
}
