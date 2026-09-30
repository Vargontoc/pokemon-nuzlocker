package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.in.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import es.vargontoc.pokemon.nuzlocker.application.ports.in.pokedex.PokedexImportPort;

@Component
public class PokedexImportRunner implements ApplicationRunner {
    
    static final Logger log = LoggerFactory.getLogger(PokedexImportRunner.class);
    final PokedexImportPort importPort;
    
    public PokedexImportRunner(PokedexImportPort importPort) {
        this.importPort = importPort;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(importPort.isComplete()) {
            log.info("Pokédex ya importada");
            return;
        }

        log.info("Pokédex incompleta o de otro juego: importando (puede tardar unos minutos...)");
        var i = importPort.importAll();
        log.info("Importados {} species, {} movimientos, {} habilidades", i.species(), i.moves(), i.abilities());
    }

    
}
