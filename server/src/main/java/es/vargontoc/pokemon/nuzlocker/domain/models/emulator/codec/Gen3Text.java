package es.vargontoc.pokemon.nuzlocker.domain.models.emulator.codec;

import java.util.Arrays;

/** Juego de caracteres de Gen 3 (versiones occidentales). Suficiente para motes; los acentos son aproximados. */
public final class Gen3Text {
    
    private static final int TERMINATOR = 0xFF;
    private static final char[] TABLE = new char[256];

    static {
        Arrays.fill(TABLE, '?');
        TABLE[0x00] = ' ';
        for (int i = 0; i < 10; i++) TABLE[0xA1 + i] = (char) ('0' + i);
        for (int i = 0; i < 26; i++) {
            TABLE[0xBB + i] = (char) ('A' + i);
            TABLE[0xD5 + i] = (char) ('a' + i);
        }
        TABLE[0xAB] = '!';
        TABLE[0xAC] = '?';
        TABLE[0xAD] = '.';
        TABLE[0xAE] = '-';
        TABLE[0xB8] = ',';
        TABLE[0xBA] = '/';
        String upper = "ÀÁÂÇÈÉÊËÌ";
        for (int i = 0; i < upper.length(); i++) TABLE[0x01 + i] = upper.charAt(i);
        TABLE[0x0B] = 'Î'; TABLE[0x0C] = 'Ï'; TABLE[0x0D] = 'Ò'; TABLE[0x0E] = 'Ó'; TABLE[0x0F] = 'Ô';
        TABLE[0x11] = 'Ù'; TABLE[0x12] = 'Ú'; TABLE[0x13] = 'Û'; TABLE[0x14] = 'Ñ';
        TABLE[0x16] = 'à'; TABLE[0x17] = 'á'; TABLE[0x19] = 'ç'; TABLE[0x1A] = 'è'; TABLE[0x1B] = 'é';
        TABLE[0x1C] = 'ê'; TABLE[0x1D] = 'ë'; TABLE[0x1E] = 'ì'; TABLE[0x20] = 'î'; TABLE[0x21] = 'ï';
        TABLE[0x22] = 'ò'; TABLE[0x23] = 'ó'; TABLE[0x24] = 'ô'; TABLE[0x26] = 'ù'; TABLE[0x27] = 'ú';
    }

    public static String decode(byte[] bytes, int offset, int maxLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < offset + maxLength; i++) {
            int b = bytes[i] & 0xFF;
            if (b == TERMINATOR) {
                break;
            }
            sb.append(TABLE[b]);
        }
        return sb.toString().trim();
    }
}
