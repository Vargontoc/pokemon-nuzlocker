package es.vargontoc.pokemon.nuzlocker.application.services;

import org.springframework.stereotype.Service;

import es.vargontoc.pokemon.nuzlocker.application.ports.in.SpecieFamilyUseCase;

@Service
public class PokeDataService implements SpecieFamilyUseCase {

    @Override
    public String familyOf(String specie) {
        return specie;
    }
    
}
