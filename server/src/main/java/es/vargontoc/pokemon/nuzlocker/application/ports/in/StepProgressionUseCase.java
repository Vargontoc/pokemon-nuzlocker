package es.vargontoc.pokemon.nuzlocker.application.ports.in;

import java.util.List;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Step;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunNuzlocke;

public interface StepProgressionUseCase {
    
    List<String> advance(RunNuzlocke run);

    Step currentStep(RunNuzlocke run);

}
