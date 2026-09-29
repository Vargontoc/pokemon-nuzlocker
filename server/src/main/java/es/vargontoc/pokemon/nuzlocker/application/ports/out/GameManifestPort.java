package es.vargontoc.pokemon.nuzlocker.application.ports.out;

import es.vargontoc.pokemon.nuzlocker.domain.models.GameManifest;

public interface GameManifestPort {

    /**
    * Carga el manifiesto del juego por su id games/<gameId>
    * @param gameId identificador del juego
    * */
    GameManifest load(String gameId);
}
