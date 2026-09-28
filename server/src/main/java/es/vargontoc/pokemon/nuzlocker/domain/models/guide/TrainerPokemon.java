package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record TrainerPokemon(String specie, String playerStarter, int level, String ability, String item, String[] moves) {
}
