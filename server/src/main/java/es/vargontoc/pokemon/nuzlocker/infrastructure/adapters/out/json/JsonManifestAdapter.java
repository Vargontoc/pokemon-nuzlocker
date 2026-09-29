package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.GameManifestPort;
import es.vargontoc.pokemon.nuzlocker.domain.models.ActiveGame;
import es.vargontoc.pokemon.nuzlocker.domain.models.GameManifest;
import tools.jackson.databind.ObjectMapper;

@Component
public class JsonManifestAdapter implements GameManifestPort {

    @Override
    public GameManifest load(String gameId) {
        String path = ActiveGame.path(gameId, "game.json");

        try(InputStream in = new ClassPathResource(path).getInputStream()){
            GameManifest gm = new ObjectMapper().readValue(in, GameManifest.class);
            if(!gameId.equals(gm.id()))
                throw new IllegalStateException("%s declara id '%s'".formatted(path, gm.id()));
            return gm;
        } catch (IOException e) {
            throw new UncheckedIOException("No existe el paquete de juego '%s' (%s)".formatted(gameId, path), e);
        }
    }
    
}
