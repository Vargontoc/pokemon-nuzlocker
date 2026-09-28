package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Datos del gimnasio

 * @param number Número de orden del gimansio
 * @param type Tipo Pokémon de gimnasio
 * @param leaderTrainerId ID del líder del gimnasio
 * @param badge ID de la medalla
 * @param badgeEs ID de la medalla en español
 * @param rewardTm MT que recompensa el gimnasio
 */
@JsonInclude(Include.NON_NULL)
public record Gym(int number, String type, String leaderTrainerId, String badge, String badgeEs, GymReward rewardTm) {
    
}
