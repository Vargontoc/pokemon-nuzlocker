package es.vargontoc.pokemon.nuzlocker.domain.models.agents;

import es.vargontoc.pokemon.nuzlocker.domain.models.agents.enums.SituationType;

/** Lo que está pasando en el juego */
public record Situation(SituationType type, String location, String specie, Integer level, boolean shiny, String trainer, String note) {
    public void validate() {
        if(type == null)
            throw new IllegalArgumentException("La situación necesita type");

        if(type == SituationType.WILD_ENCOUNTER && (location == null || specie == null))
            throw new IllegalArgumentException("WILD_EMCOUNTER necesita locationId y species");

        if(type == SituationType.TRAINER_AHEAD && trainer == null)
            throw new IllegalArgumentException("TRAINER_AHEAD necesita trainer");
    }

    /** Pregunta para el guide-agent */
    public String guideQuestion() {
        return switch(type) {
            case STARTED_CHOICE -> "¿Qué dice la guçia sobre la elección del inicial y los primeros gimnasios?";
            case WILD_ENCOUNTER -> "Encuentro salvaje de %s (nivel %s) en %s. ¿Qué encuentros tiene esta zona y qué combates vienen después?".formatted(specie, level, location);
            case TRAINER_AHEAD -> "combate próximo contra %s. ¿Qué equipo lleva y es obligatorio?".formatted(trainer);
            case NEXT_STEP -> "¿Cuál es el objetivo actual y qué comvate y encuentros hay hasta el sigguienter paso?";
        } + (note != null ? "Nota: " + note: "");
    }
}
