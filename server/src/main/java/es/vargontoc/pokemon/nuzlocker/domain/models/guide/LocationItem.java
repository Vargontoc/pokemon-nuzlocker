package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import java.util.List;


public record LocationItem(String item, String name, String source, boolean hidden, List<Requirement> requires) {
}
