package es.vargontoc.pokemon.nuzlocker.domain.models.emulator.profile;


import java.io.IOException;
import java.util.Map;


/**
 * Mapa de memoria de una versión concreta de una ROM (games/<id>/profiles/<codigo>-<revision>.json)
 * Los símbolos tienen nombres comunes para toda una generación, cada perfíl da sus direcciones
 * GameProfile
 * @param romCode
 * @param revision
 * @param description
 * @param pointerSize
 * @param pointerMin
 * @param pointerMax
 * @param symbols
 * @param sizes
 */
public record GameProfile(String romCode, int revision, String description, int pointerSize, Long pointerMin, Long pointerMax, Map<String, SymbolExpression> symbols, Map<String, Integer> sizes) {
    public String key() {
        return romCode + "-" + revision;
    }

    public long address(String symbol, SymbolExpression.PointerReader reader) throws IOException {
        SymbolExpression  expression = symbols.get(symbol);
        if(expression == null)
            throw new IllegalStateException("El perfil %s no define el símbolo '%s'".formatted(key(), symbol));
        return expression.resolve(reader);
    }

    public int size(String name) {
        Integer size = sizes.get(name);
        if(size == null)
            throw new IllegalStateException("El perfil %s no define el tamaño '%s'".formatted(key(), name));
        return size;
    }

    public void checkPointer(long pointer) {
        if((pointerMin != null && pointer < pointerMin) || (pointerMax != null && pointer >= pointerMax))
            throw new IllegalStateException("Puntero fuera de rango en %s: 0x%08X ¿Reiniciando juego?".formatted(key(), pointer));
    }
}
