package es.vargontoc.pokemon.nuzlocker.domain.models;

/**
 *
 *  Juego activo
 */
public class ActiveGame {
    private final GameManifest manifest;

    public ActiveGame(String gameId) {
        this.manifest = GameManifest.load(gameId);
    }

    /**
     * Obtiene el identificador del juego
     */
    public String id() { return manifest.id(); }
    /*
    * Obtiene el manifiesto del juego
    */
    public GameManifest manifest() { return manifest; }

    /* 
    Obtiene la ruta de un recurso dentro del paquete del juego
    */
    public static String path(String gameId, String relative) { return "games/" + gameId + "/" + relative; }

}
