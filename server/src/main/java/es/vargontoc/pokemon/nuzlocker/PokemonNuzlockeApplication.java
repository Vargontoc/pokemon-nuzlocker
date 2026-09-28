package es.vargontoc.pokemon.nuzlocker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages= "es.vargontoc")
@ConfigurationPropertiesScan("es.vargontoc")
public class PokemonNuzlockeApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PokemonNuzlockeApplication.class, args);
    }
}
