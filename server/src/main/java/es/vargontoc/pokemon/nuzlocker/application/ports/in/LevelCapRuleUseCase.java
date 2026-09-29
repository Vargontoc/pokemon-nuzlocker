package es.vargontoc.pokemon.nuzlocker.application.ports.in;

import java.util.OptionalInt;

import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunNuzlocke;

public interface LevelCapRuleUseCase {
    
    OptionalInt capFor(RunNuzlocke run);
}
