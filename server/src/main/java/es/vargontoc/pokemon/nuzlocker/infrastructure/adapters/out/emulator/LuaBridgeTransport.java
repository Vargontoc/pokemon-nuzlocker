package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.transport.EmulatorTransportPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.emulator.RomId;
import es.vargontoc.pokemon.nuzlocker.infrastructure.configuration.EmulatorProperties;

/**
 * Transporte TCP para los scipts Lua del puente.
 * Protocolo de texto, una orden por linea: PING, INFO, READ <hex> <n>, PRESS <button> <frames>.
 * Funciona con cualquier emulador que trabaje en este protocolo.
 * LuaBridgeTransport
 */
@Component
public class LuaBridgeTransport implements EmulatorTransportPort{

    static final int READ_TIMEOUT_MS = 2_000;
    private final EmulatorProperties props;
    private Socket socket;
    private BufferedReader in;
    private Writer out;

    public LuaBridgeTransport(EmulatorProperties props) {
        this.props = props;
    }

    @Override
    public RomId info() throws IOException {
        return RomId.parse(payload(send("INFO")));
    }

    @Override
    public byte[] read(long address, int length) throws IOException {
        return HexFormat.of().parseHex(payload(send("READ %08X %d".formatted(address, length))));
    }

    @Override
    public void press(String button, int frames) throws IOException {
        send("PRESS %s %d".formatted(button, frames));
    }

        @Override
    public void close() {
       try {
        if(socket != null)
            socket.close();
       }catch(IOException ignored){
        // Se cierra igualmente
       }finally {
            socket = null;
       }
    }

    synchronized String send(String command) throws IOException {
        ensureConnected();
        try {
            out.write(command + "\n");
            out.flush();

            String line = in.readLine();
            if(line == null)
                throw new EOFException("El emulador cerró la conexión");

            if(line.startsWith("ERR"))
                throw new IOException("Emulador: " + line);
            return line;
        }catch(IOException e){
            close();
            throw e;
        }
    }

    static String payload(String line) {
        return line.startsWith("OK") ? line.substring(2).trim() : line;
    }

    void ensureConnected() throws IOException {
        if(socket != null && socket.isConnected() && !socket.isClosed())
            return;

        Socket s = new Socket();
        s.connect(new InetSocketAddress(props.host(), props.port()), props.connectTimeoutMs());
        s.setSoTimeout(READ_TIMEOUT_MS);

        socket = s;
        in  = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.US_ASCII));
        out = new OutputStreamWriter(s.getOutputStream(), StandardCharsets.US_ASCII);
    }

    
}
