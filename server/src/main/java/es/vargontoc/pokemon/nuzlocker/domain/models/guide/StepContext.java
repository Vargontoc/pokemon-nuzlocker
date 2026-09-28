package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record StepContext(Step step, Location location, List<Trainer> trainers) {
}
