package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.codec.GenerationCodec;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.reader.EmulationReader;
import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.transport.EmulatorTransportPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.GameSnapshot;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PartyMon;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.profile.GameProfile;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.profile.SymbolExpression;

@Component
public class Gen3Reader implements EmulationReader {

    private static final int MAX_PARTY = 6;
    private static final long BATTLE_TYPE_TRAINER = 0x08;


    @Override
    public int generation() {
        return 3;
    }

    @Override
    public GameSnapshot read(EmulatorTransportPort transport, GameProfile profile, GenerationCodec codec)
            throws IOException {
        SymbolExpression.PointerReader pointers = address -> {
            long pointer = littleEndian(transport.read(address, profile.pointerSize()));
            profile.checkPointer(pointer);
            return pointer;
        };
        int monSize = codec.monSize();

        int count = Math.min(transport.read(profile.address("partyCount", pointers), 1)[0] & 0xFF, MAX_PARTY);
        byte[] partyBytes = transport.read(profile.address("party", pointers), MAX_PARTY * monSize);
        List<PartyMon> party = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            PartyMon mon = codec.decode(Arrays.copyOfRange(partyBytes, i * monSize, (i + 1) * monSize));
            if (mon != null) {
                party.add(mon);
            }
        }

        PartyMon enemy = codec.decode(transport.read(profile.address("enemyParty", pointers), monSize));
        long battleFlags = littleEndian(transport.read(profile.address("battleTypeFlags", pointers), 4));
        byte[] location = transport.read(profile.address("mapLocation", pointers), 2);
        byte[] flags = transport.read(profile.address("flags", pointers), profile.size("flags"));

        return new GameSnapshot(profile.key(), List.copyOf(party), enemy, (battleFlags & BATTLE_TYPE_TRAINER) != 0,
                location[0] & 0xFF, location[1] & 0xFF, flags, Instant.now());
    }

    static long littleEndian(byte[] bytes) {
        ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        return switch (bytes.length) {
            case 2 -> Short.toUnsignedInt(buf.getShort());
            case 4 -> Integer.toUnsignedLong(buf.getInt());
            default -> throw new IllegalArgumentException("Tamaño de puntero no soportado: " + bytes.length);
        };
    }
    
}
