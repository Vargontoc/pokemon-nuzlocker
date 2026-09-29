package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.emulator;

import java.util.List;

import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.emulator.codec.GenerationCodec;

@Component
public class CodecRegistry {
    
    final List<GenerationCodec> codecs;

    public CodecRegistry(List<GenerationCodec> codecs) {
        this.codecs  = codecs;
    }

    public GenerationCodec forGeneration(int generation) {
        return codecs.stream().filter(c -> c.generation() == generation).findFirst().orElseThrow(() -> new IllegalStateException("No hay codec para la generación " + generation));
    }
}
