package es.vargontoc.pokemon.nuzlocker.domain.models.agents;

/** Validación de la decisión de un Agente */
public record Validation(boolean valid, String reason) {
    public static Validation ok(){
        return new Validation(true, null);
    }

    public static Validation reject(String reason) {
        return new Validation(false, reason);
    }
}
