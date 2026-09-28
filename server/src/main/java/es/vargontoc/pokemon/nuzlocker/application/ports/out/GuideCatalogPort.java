package es.vargontoc.pokemon.nuzlocker.application.ports.out;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Location;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Step;
import es.vargontoc.pokemon.nuzlocker.domain.models.guide.Trainer;

/*
Interfaz de los datos de una guía Pokémon con los datos necesarios para el Nuzlocke
*/
public interface GuideCatalogPort {

    /*
    Obtiene el identificador del juego
    */
    public String gameId();

    /**
    Obtiene una localización por su identificador
    @param id identificador único de la localización
    */
    Optional<Location> location(String id);

    /**
    Obtiene todas las localizaciones de la guía
    */
    Collection<Location> locations();

    /**
     * Obtiene un entrenador por su identificador
     * @param id identificador único del entrenador
     * @return entrenador
     */
    Optional<Trainer> trainer(String id);

    /**
     * Obtiene un paso de la guía por su identificador unico
     * @param id identificador del paso
     * @return paso de la guía
     */
    Optional<Step> step(String id);

    /**
     * Obtiene un paso de la guía buscando por su orden
     * @param order 
     * @return
     */
    Optional<Step> stepByOrder(int order);

    /**
     * Obtiene todos los pasos de la guía
     * @return lista de pasos
     */
    List<Step> steps();

    /**
     * Obtiene los avisos producidos en la carga de la guía
     * @return lista de avisos
     */
    List<String> warnings();
}
