package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.LocationType;

@JsonInclude(Include.NON_NULL)
public record Location(String id, String name, LocationType type, String parentId, String summary, List<Connection> connections, List<String> services, Gym gym, List<LocationItem> items, List<Encounterslot> encounters) {
    
}
