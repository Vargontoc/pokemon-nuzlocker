package es.vargontoc.pokemon.nuzlocker.domain.models;

import java.util.List;



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
}
