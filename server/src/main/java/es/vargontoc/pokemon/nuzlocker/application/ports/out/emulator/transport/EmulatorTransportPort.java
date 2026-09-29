package es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.transport;

import java.io.IOException;

import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.RomId;

/**
 * Interfaz con metodos para acceso de bajo nivel a un emulador, identificar la ROM, leer memoria y pulsar botones.
 * Es agnostica al tipo juego, cada emulador aporta su script con el mismo protocolo
 * EmulatorTransportPort
 */
public interface EmulatorTransportPort extends AutoCloseable {

    RomId info() throws IOException;

    byte[] read(long address, int length) throws IOException;

    void press(String button, int frames) throws IOException;

    @Override
    void close();
}
