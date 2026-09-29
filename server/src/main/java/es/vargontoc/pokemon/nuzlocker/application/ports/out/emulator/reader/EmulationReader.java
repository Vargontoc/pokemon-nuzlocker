package es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.reader;

import java.io.IOException;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.codec.GenerationCodec;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.transport.EmulatorTransportPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.GameSnapshot;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.profile.GameProfile;

/**
 *
 * EmulationReader
 */
public interface EmulationReader {
    
    int generation();

    GameSnapshot read(EmulatorTransportPort port, GameProfile profile, GenerationCodec codec) throws IOException;

}
