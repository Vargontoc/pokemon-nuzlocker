package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import java.util.List;


public record StepContext(Step step, Location location, List<Trainer> trainers) {
}
