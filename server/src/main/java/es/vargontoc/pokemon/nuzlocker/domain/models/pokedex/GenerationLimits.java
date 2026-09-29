package es.vargontoc.pokemon.nuzlocker.domain.models.pokedex;

public record GenerationLimits(int species, int moves, int abilities) {
    public static GenerationLimits of(int generation){
        return switch (generation){
            case 1 -> new GenerationLimits(151, 165, 0);
            case 2 -> new GenerationLimits(251, 251, 0);
            case 3 -> new GenerationLimits(386, 354, 76);
            case 4 -> new GenerationLimits(493, 467, 123);
            case 5 -> new GenerationLimits(649, 559, 164);
            default -> throw new IllegalArgumentException("Generación no soportada: " + generation);
        };
    }
}
