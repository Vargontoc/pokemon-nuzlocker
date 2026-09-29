package es.vargontoc.pokemon.nuzlocker.domain.models.emulator.profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



/**
 * Dirección de memoria expresada como cadena de punteros
 * 0x02024284 dirección fija
 * [0x03005008] + 0xEE0 lee el puntero y suma 0xEE0
 * [[0x0300500C] + 0x10] - 4 punteros anidados
 * En muchos juegos las estructuras se mueven en memoria
 * SymbolExpression
 */
public final class SymbolExpression {
    
    @FunctionalInterface
    public interface PointerReader {
        long readPointer(long address) throws IOException;
    }

    private sealed interface Node permits Literal, Deref, Offset {}

    private record Literal(long value) implements Node {}

    private record Deref(Node inner) implements Node {}

    private record Offset(Node base, long delta) implements Node {}

    final String text;
    final  Node root;

    public SymbolExpression(String text, Node root) {
        this.text = text;
        this.root = root;
    }

    public static SymbolExpression parse(String text) {
        Parser parser = new Parser(tokenize(text), text);
        Node node = parser.expression();
        parser.expectEnd();;
        return new SymbolExpression(text, node);
    }

    public long resolve(PointerReader reader) throws IOException {
        return resolve(root, reader);
    }

    public boolean isFixed() {
        return root instanceof Literal;
    }

    @Override
    public String toString() {
        return text;
    }

    static long resolve(Node node, PointerReader reader) throws IOException{
        return switch(node) {
            case Literal l -> l.value();
            case Deref d -> reader.readPointer(resolve(d.inner, reader));
            case Offset o -> resolve(o.base(), reader) + o.delta();
        };
    }


    static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while(i < text.length()) {
            char c = text.charAt(i);
            if(Character.isWhitespace(c))
                i++;
            else if("[]+-".indexOf(c) >= 0){
                tokens.add(String.valueOf(c));
                i++;
            }else {
                int start = i;
                while (i < text.length() && Character.isLetterOrDigit(text.charAt(i))) {
                    i++;
                }
                if(start == i) {
                    throw new IllegalArgumentException("Carácter inesperado '%s' en '%s'".formatted(c, text));
                }
                tokens.add(text.substring(start, i));

            }
        }
        return tokens;
    }


    static final class Parser {
        final List<String> tokens;
        final String text;
        private int pos;

        Parser(List<String> tokens, String text) {
            this.tokens = tokens;
            this.text = text;
        }

        Node expression(){
            Node node = term();
            while(peek("+") || peek("-")) {
                boolean plus = next().equals("+");
                long value = number();
                node = new Offset(node, plus ? value: -value);
            }
            return node;
        }

        Node term() {
            if(peek("[")) {
                next();
                Node inner = expression();
                if(!peek("]")) {
                    throw error("falta ']'");
                }
                next();
                return new Deref(inner);
            }
            return new Literal(number());
        }

        long number() {
            if(pos > tokens.size())
                throw error("se esperaba un número");
            try {
                return Long.decode(next());
            }catch(NumberFormatException e) {
                throw error("número no válido");
            }
        }

        void expectEnd() {
            if(pos < tokens.size()) 
                throw error("sobra '" + tokens.get(pos) + "'");
        }

        boolean peek(String token){
            return pos < tokens.size() && tokens.get(pos).equals(token);
        }

        String next() {
            return tokens.get(pos++);
        }

        IllegalArgumentException error(String message){
            return new IllegalArgumentException("Expresión de símbolo no válida '%s': %s".formatted(text, message));
        }
    }
}
