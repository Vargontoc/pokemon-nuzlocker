package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities;

import es.vargontoc.framework.persistence.BaseEntity;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunPokemon;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.RunPokemonStatusType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name= "run_pokemon")
public class RunPokemonJpaEntity extends BaseEntity<Long> {

    @Column(name = "identity_key")
    private String identityKey;
    
    @Column(name= "run_id")
    private Long runId;

    @Column(name = "specie")
    private String specie;

    @Column(name = "nickname")
    private String nickName;

    @Column(name = "level")
    private int level;

    @Enumerated(EnumType.STRING)
    @Column(name= "status")
    private RunPokemonStatusType status;

    @Column(name = "is_shiny")
    private boolean shiny;

    @Column(name = "location_id")
    private String location;

    @Column(name = "caught_at_step")
    private int caughtAtStep;

    @Column(name = "cause_of_death")
    private String causeOfDeath;

    public void of(RunPokemon pokemon) {
        this.setCaughtAtStep(pokemon.caughtAtStep());
        this.setCauseOfDeath(pokemon.causeofDeath());
        this.setId(pokemon.id());
        this.setIdentityKey(pokemon.identityKey());
        this.setLevel(pokemon.level());
        this.setNickName(pokemon.nickname());
        this.setLocation(pokemon.locationId());
        this.setRunId(pokemon.runId());
        this.setShiny(pokemon.shiny());
        this.setSpecie(pokemon.specie());
        this.setStatus(pokemon.status());
    }

    public String getIdentityKey() {
        return identityKey;
    }

    public void setIdentityKey(String identityKey) {
        this.identityKey = identityKey;
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public String getSpecie() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie = specie;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public RunPokemonStatusType getStatus() {
        return status;
    }

    public void setStatus(RunPokemonStatusType status) {
        this.status = status;
    }

    public boolean isShiny() {
        return shiny;
    }

    public void setShiny(boolean shiny) {
        this.shiny = shiny;
    }

    public int getCaughtAtStep() {
        return caughtAtStep;
    }

    public void setCaughtAtStep(int caughtAtStep) {
        this.caughtAtStep = caughtAtStep;
    }

    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    public void setCauseOfDeath(String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    
    
}
