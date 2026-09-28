package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record ItemMarket(String item, int price) {
}
