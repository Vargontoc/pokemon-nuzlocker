package es.vargontoc.pokemon.nuzlocker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages= "es.vargontoc")
@ConfigurationPropertiesScan("es.vargontoc")
@EnableJpaAuditing
@EnableScheduling
public class PokemonNuzlockeApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PokemonNuzlockeApplication.class, args);
    }
}
