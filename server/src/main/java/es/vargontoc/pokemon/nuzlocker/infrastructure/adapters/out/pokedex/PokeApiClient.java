package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.pokedex;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import es.vargontoc.pokemon.nuzlocker.application.ports.out.pokedex.PokedeLoaderPort;
import es.vargontoc.pokemon.nuzlocker.infrastructure.configuration.PokeApiProperties;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class PokeApiClient implements PokedeLoaderPort {

    static final String API_PREFIX = "/api/v2/";

    final RestClient http;
    final JdbcTemplate jdbc;
    final ObjectMapper mapper;
    final String baseUrl;
    final long requestDelayMs;

    public PokeApiClient(PokeApiProperties properties, JdbcTemplate jdbc, ObjectMapper mapper, RestClient.Builder client){
        this.http = client.build();
        this.jdbc = jdbc;
        this.mapper = mapper;
        this.baseUrl = properties.baseUrl();
        this.requestDelayMs = properties.requestDelayMs();

    }

    @Override
    public JsonNode get(String path) {
        String normalized = normalize(path);
        List<String> cached = jdbc.queryForList("select body::text from pokeapi_cache where path = ?", String.class, normalized);
        String body = cached.isEmpty() ? download(normalized) : cached.getFirst();
        try{
            return mapper.readTree(body);
        }catch(Exception e) {
            throw new IllegalStateException("Respuesta inválida de PokeApi para " + normalized, e);
        }
    }

    @Override
    public JsonNode getUrl(String url) {
       int idx = url.indexOf(API_PREFIX);
       return get(idx >= 0 ? url.substring(idx + API_PREFIX.length()) : url);
    }

    private String download(String path){
        pause();
        String body = http.get().uri(baseUrl + "/" + path + "/").retrieve().body(String.class);
        jdbc.update("insert into pokeapi_cache (path, body, fetched_at) values (?, ?::jsonb, now()) on conflict (path) do nothing", path, body);
        return body;
    }

    private void pause() {
        if(requestDelayMs <= 0)
            return;

        try {
            Thread.sleep(requestDelayMs);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Importación interrumpida", e);
        }
    }

    static String normalize(String path){
        String p = path.startsWith("/") ? path.substring(1) : path;
        return p.endsWith("/") ? p.substring(0, p.length() -1) : p;
    }
    
}
