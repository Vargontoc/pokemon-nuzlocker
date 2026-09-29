package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.codec.GenerationCodec;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.PartyMon;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.codec.Gen3Text;

@Component
public class Gen3Codec implements GenerationCodec {

    public static final int SIZE = 100;
    static final String[] ORDERS = {
            "GAEM", "GAME", "GEAM", "GEMA", "GMAE", "GMEA", "AGEM", "AGME", "AEGM", "AEMG", "AMGE", "AMEG",
            "EGAM", "EGMA", "EAGM", "EAMG", "EMGA", "EMAG", "MGAE", "MGEA", "MAGE", "MAEG", "MEGA", "MEAG"};

    private static final int DATA_OFFSET = 32;
    private static final int DATA_SIZE = 48;
    private static final int SUBSTRUCT_SIZE = 12;
    private static final int LAST_KANTO_JOHTO_SPECIES = 251;

    @Override
    public int generation() {
        return 3;
    }

    @Override
    public int monSize() {
        return SIZE;
    }


    @Override
    public PartyMon decode(byte[] raw) {
        ByteBuffer buf = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN);
        long personality = Integer.toUnsignedLong(buf.getInt(0));
        long otId = Integer.toUnsignedLong(buf.getInt(4));
        if (personality == 0 && otId == 0) {
            return null;
        }

        byte[] data = new byte[DATA_SIZE];
        ByteBuffer dec = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int key = (int) (personality ^ otId);
        for (int i = 0; i < DATA_SIZE; i += 4) {
            dec.putInt(i, buf.getInt(DATA_OFFSET + i) ^ key);
        }

        int sum = 0;
        for (int i = 0; i < DATA_SIZE; i += 2) {
            sum += Short.toUnsignedInt(dec.getShort(i));
        }
        boolean checksumOk = (sum & 0xFFFF) == Short.toUnsignedInt(buf.getShort(28));

        String order = ORDERS[(int) (personality % 24)];
        int growth = order.indexOf('G') * SUBSTRUCT_SIZE;
        int attacks = order.indexOf('A') * SUBSTRUCT_SIZE;

        int internalSpecies = Short.toUnsignedInt(dec.getShort(growth));
        if (internalSpecies == 0) {
            return null;
        }
        List<Integer> moves = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            moves.add(Short.toUnsignedInt(dec.getShort(attacks + i * 2)));
        }

        return new PartyMon(Long.toString(personality), nationalDex(internalSpecies), Gen3Text.decode(raw, 8, 10),
                raw[84] & 0xFF, Short.toUnsignedInt(buf.getShort(86)), Short.toUnsignedInt(buf.getShort(88)),
                List.copyOf(moves), isShiny(personality, otId), checksumOk);
    }

    static boolean isShiny(long personality, long otId) {
        long value = (otId >>> 16) ^ (otId & 0xFFFF) ^ (personality >>> 16) ^ (personality & 0xFFFF);
        return value < 8;
    }

    /** El índice interno coincide con la Pokédex nacional hasta el 251; Hoenn va en otro orden (pendiente). */
    private static int nationalDex(int internal) {
        return internal <= LAST_KANTO_JOHTO_SPECIES ? internal : -1;
    }

    
}
