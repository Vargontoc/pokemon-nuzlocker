package es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.codec;

import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PartyMon;

/** Formato binario de un Pokémon de equipo en una generaciñon */
public interface GenerationCodec {
    
    int generation();

    /** Bytes que ocupa un Pokémon de equipo */
    int monSize();

    /**
     * @return null si el hueco está vacio
     */
    PartyMon decode(byte[] raw);
}
