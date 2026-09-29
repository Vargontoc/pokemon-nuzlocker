package es.vargontoc.pokemon.nuzlocker.application.dto;

import java.util.List;

public record EventResult(List<String> completedSteps, RunStateView state) {
}
