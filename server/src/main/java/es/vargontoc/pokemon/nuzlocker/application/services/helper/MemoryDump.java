package es.vargontoc.pokemon.nuzlocker.application.services.helper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.transport.EmulatorTransportPort;

public record MemoryDump(long start, byte[] bytes) {

    private static final int CHUNK = 4096;

    public static MemoryDump read(EmulatorTransportPort transport, long start, long end) throws IOException {
        byte[] all = new byte[(int) (end - start)];
        for (long address = start; address < end; address += CHUNK) {
            byte[] part = transport.read(address, (int) Math.min(CHUNK, end - address));
            System.arraycopy(part, 0, all, (int) (address - start), part.length);
        }
        return new MemoryDump(start, all);
    }

    public long end() {
        return start + bytes.length;
    }

    public boolean contains(long address, int length) {
        return address >= start && address + length <= end();
    }

    public byte[] slice(long address, int length) {
        int offset = (int) (address - start);
        return Arrays.copyOfRange(bytes, offset, offset + length);
    }

    public int u8(long address) {
        return bytes[(int) (address - start)] & 0xFF;
    }

    public long u32(long address) {
        return Integer.toUnsignedLong(ByteBuffer.wrap(bytes, (int) (address - start), 4).order(ByteOrder.LITTLE_ENDIAN).getInt());
    }
}
