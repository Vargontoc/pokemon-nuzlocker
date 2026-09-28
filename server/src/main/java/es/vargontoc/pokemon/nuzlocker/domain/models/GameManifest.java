package es.vargontoc.pokemon.nuzlocker.domain.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import tools.jackson.databind.ObjectMapper;

/**
 * 
 * Descripción de un juego
 * @param id identificador del juego
 * @param name nombre del juego
 * @param generation generación Pokémon que pertenece el juego
 * @param platform plataforma que pertenece el juego
 * @param version versión del juego
 * @param romCodes códigos de cartucho que pertenecen al juego (todos los idiomas)
 * @param starters los Pokémon starters que ofrecen en ese juego
 * @param unsupportedRules reglas que no se pueden aplicar al juego
 */
public record GameManifest(String id, String name, int generation, String platform, String version, List<String> romCodes, List<String> starters, List<String> unsupportedRules) {
    
    /*
    * Carga el manifiesto del juego por su id games/<gameId>
    */
    public static GameManifest load(String gameId) {
        String path = ActiveGame.path(gameId, "game.json");

        try(InputStream in = new ClassPathResource(path).getInputStream()){
            GameManifest gm = new ObjectMapper().readValue(in, GameManifest.class);
            if(!gameId.equals(gm.id()))
                throw new IllegalStateException("%s declara id '%s'".formatted(path, gm.id()));
            return gm;
        } catch (IOException e) {
            throw new UncheckedIOException("No existe el paquete de juego '%s' (%s)".formatted(gameId, path), e);
        }
    }

}
