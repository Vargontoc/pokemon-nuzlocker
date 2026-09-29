package es.vargontoc.pokemon.nuzlocker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages= "es.vargontoc")
@ConfigurationPropertiesScan("es.vargontoc")
@EnableJpaAuditing
public class PokemonNuzlockeApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PokemonNuzlockeApplication.class, args);
    }
}
