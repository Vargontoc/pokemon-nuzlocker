package es.vargontoc.pokemon.nuzlocker.application.services;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.codec.GenerationCodec;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.reader.EmulationReader;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.transport.EmulatorTransportPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.ActiveGame;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.GameSnapshot;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.profile.GameProfile;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator.CodecRegistry;
import es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator.ProfileRegistry;

@Component
public class SnapshotService {
    
    private final EmulatorTransportPort transport;
    private final ProfileRegistry profiles;
    private final CodecRegistry codecs;
    private final List<EmulationReader> readers;
    private final ActiveGame game;

    private GameProfile profile;
    private GenerationCodec codec;
    private EmulationReader reader;

    public SnapshotService(EmulatorTransportPort transport, ProfileRegistry profiles, CodecRegistry codecs,
                        List<EmulationReader> readers, ActiveGame game) {
        this.transport = transport;
        this.profiles = profiles;
        this.codecs = codecs;
        this.readers = readers;
        this.game = game;
    }

        public synchronized GameSnapshot read() throws IOException {
        if (profile == null) {
            int generation = game.manifest().generation();
            GameProfile detected = profiles.require(transport.info());
            codec = codecs.forGeneration(generation);
            reader = readers.stream()
                    .filter(r -> r.generation() == generation)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No hay lector para la generación " + generation));
            profile = detected;
        }
        return reader.read(transport, profile, codec);
    }

    /** Tras un error se vuelve a identificar la ROM: puede haberse cambiado en el emulador. */
    public synchronized void reset() {
        profile = null;
        transport.close();
    }

}
