package es.vargontoc.pokemon.nuzlocker.application.services;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import es.vargontoc.pokemon.nuzlocker.application.ports.in.SpecieFamilyUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.in.pokedex.LookupUseCase;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.AbilityPort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.MovePort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.SpeciePort;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Ability;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.LookupResult;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Move;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Specie;

@Service
public class PokeDataService implements SpecieFamilyUseCase, LookupUseCase {

    final SpeciePort species;
    final MovePort moves;
    final AbilityPort abilities;

    private volatile Map<String, LookupResult> index;

    public PokeDataService(SpeciePort species, MovePort moves, AbilityPort abilities) {
        this.species = species;
        this.moves = moves;
        this.abilities = abilities;
    }

    @Override
    public String familyOf(String specie) {
        return species.findBySlug(specie).map(e -> e.family()).orElse(specie);
    }

    @Override
    public Optional<LookupResult> lookup(String name) {
        return Optional.ofNullable(index().get(normalize(name)));
    }

    @Override
    public void invalidate() {
        index = null;
    }

    Map<String, LookupResult> index() {
        Map<String, LookupResult> current = index;

        if(current == null)  {
            current = new HashMap<>();
            for(Specie s: species.findAll()) put(current, new LookupResult("species", s.slug(), s.name()));
            for(Move s: moves.findAll()) put(current, new LookupResult("move", s.slug(), s.name()));
            for(Ability s: abilities.findAll()) put(current, new LookupResult("ability", s.slug(), s.name()));
        }

        return current;
    }
    
    static void put(Map<String, LookupResult> map, LookupResult r) {
        map.putIfAbsent(normalize(r.name()), r);
        map.putIfAbsent(normalize(r.slug()), r);
    }

        static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")          // quita tildes
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "");     // quita espacios, guiones y signos
    }
}
