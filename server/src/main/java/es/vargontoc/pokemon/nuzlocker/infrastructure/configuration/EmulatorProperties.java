package es.vargontoc.pokemon.nuzlocker.infrastructure.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nuzlocke.emulator")
public record EmulatorProperties(boolean enabled, String host, int port, long pollIntervalMs, int connectTimeoutMs, boolean autoTurn) {
}
