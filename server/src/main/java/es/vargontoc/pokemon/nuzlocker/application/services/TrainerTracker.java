package es.vargontoc.pokemon.nuzlocker.application.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.GuideCatalogPort;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.persistence.SpeciePort;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PartyMon;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.TeamMember;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Trainer;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.TrainerPokemon;

@Component
public class TrainerTracker {
    

    final GuideCatalogPort catalog;
    final SpeciePort species;

    public TrainerTracker(GuideCatalogPort catalog, SpeciePort species) {
        this.catalog = catalog;
        this.species = species;
    }

    public Optional<Trainer> identify(List<PartyMon> team, String playerStarter) {
        List<TeamMember> members = describe(team);
        List<Trainer> matches = matches(members, catalog.trainers(), playerStarter);
        return matches.size() == 1 ? Optional.of(matches.getFirst()) : Optional.empty();
    }

    public List<TeamMember> describe(List<PartyMon> team) {
        return team.stream().map(m -> new TeamMember(species.findById(Long.valueOf(m.species())).map(s -> s.slug()).orElse("#" + m.species()), m.level())).toList();
    }

    static List<Trainer> matches(List<TeamMember> members, Collection<Trainer> trainers, String playerStarter) {
        return trainers.stream().filter(t -> sameTeam(t.team(), members, playerStarter)).toList();
    }

    static boolean sameTeam(List<TrainerPokemon> expected, List<TeamMember> actual, String playerStarter) {
        if(expected.size() != actual.size())
            return false;

        for(int i = 0; i < expected.size(); i++) {
            TrainerPokemon e = expected.get(i);
            TeamMember a = actual.get(i);
            if(e.level() != a.level() || !speciesMatches(e, a.specie(), playerStarter))
                return false;
        }
        return true;
    }

    static boolean speciesMatches(TrainerPokemon expected, String actual, String playerStarter) {
        if(expected.specie() != null || expected.playerStarter() == null)
            return expected.specie().equals(actual);
        return playerStarter != null ? actual.equals(playerStarter) : expected.playerStarter().equals(actual);
    }
}
