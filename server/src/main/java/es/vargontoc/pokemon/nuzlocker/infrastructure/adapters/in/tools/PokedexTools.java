package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.tools;

import java.util.List;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.ports.in.pokedex.LookupUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.MovePort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.SpeciePort;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.BaseStats;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.LearnEntry;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.LookupResult;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Move;

@Component
public class PokedexTools {
    
    record SummarySpecie(String slug, String name, List<String> types, BaseStats stats, List<String> abilities, String family, List<LearnEntry> levelUpMoves) {}

    final SpeciePort species;
    final MovePort moves;
    final LookupUseCase lookup;

    public PokedexTools(SpeciePort species, MovePort moves, LookupUseCase lookup) {
        this.species = species;
        this.moves = moves;
        this.lookup = lookup;
    }
    
    @Tool(name = "get_species", description = "Tipos, stats base, habilidades y movimientos por nivel de una especie Pokémon")
    public SummarySpecie getSpecie(@ToolParam(description = "slug en inglés. p. ej. pidgey") String slug)  {
        return species.findBySlug(slug).map(s ->
            new SummarySpecie(s.slug(), s.name(), s.types(), s.stats(), s.abilities(), s.family(), s.learnSet().stream().filter(f -> "level-up".equals(f.method())).toList()))
            .orElse(null);
    }

    @Tool(name = "get_move", description = "Tipo,potencia, precisión, PP y categía de un movimiento Pokémon")
    public Move getMove(@ToolParam(description = "slug en ingñes, p. ej. rock-tomb") String slug) {
        return moves.findBySlug(slug).orElse(null);
    }

    @Tool(name = "lookup_name", description = "Convierte un nombre en español (especie, movimiento, habilidad o items) en su slug")
    public LookupResult lookup(@ToolParam(description = "nombre en español") String name){
        return lookup.lookup(name).orElse(null);
    }


    

    
}
