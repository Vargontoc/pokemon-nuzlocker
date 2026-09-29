package es.vargontoc.pokemon.nuzlocker.domain.models.guide;

import java.util.List;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.enums.LocationType;


public record Location(String id, String name, LocationType type, String parentId, String summary, List<Connection> connections, List<String> services, Gym gym, List<LocationItem> items, List<Encounterslot> encounters) {
    
}
