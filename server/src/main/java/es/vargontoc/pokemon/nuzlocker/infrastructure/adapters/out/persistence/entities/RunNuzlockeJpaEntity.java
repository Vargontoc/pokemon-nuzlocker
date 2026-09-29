package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities;

import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import es.vargontoc.framework.persistence.BaseEntity;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.RunNuzlocke;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.RunStatusType;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.rules.NuzlockeRules;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "run_nuzlocke")
public class RunNuzlockeJpaEntity extends BaseEntity<Long> {
    
    @Version
    private Long version;

    @Column(name= "game_id")
    private String gameId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RunStatusType status;

    @Column(name = "starter_choosen")
    private String starter;

    @Column(name= "current_step_order")
    private int currentStepOrder;

    @Column(name = "current_location_id")
    private String currentLocation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name= "rules")
    private NuzlockeRules rules;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name= "flags")
    private Set<String> flags = Set.of();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name= "defeated_trainers")
    private Set<String> defeatedTrainers = Set.of();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name= "unlocks")
    private Set<String> unlocks = Set.of();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name= "badges")
    private Set<String> badges = Set.of();


    public void of(RunNuzlocke run) {
        this.setId(run.id());
        this.setBadges(run.badges());
        this.setCurrentLocation(run.currentLocationId());
        this.setCurrentStepOrder(run.currentStepOrder());
        this.setDefeatedTrainers(run.defeatedTrainers());
        this.setFlags(run.flags());
        this.setGameId(run.gameId());
        this.setRules(run.rules());
        this.setStarter(run.starter());
        this.setUnlocks(run.unlocks());
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public RunStatusType getStatus() {
        return status;
    }

    public void setStatus(RunStatusType status) {
        this.status = status;
    }

    public String getStarter() {
        return starter;
    }

    public void setStarter(String starter) {
        this.starter = starter;
    }

    public int getCurrentStepOrder() {
        return currentStepOrder;
    }

    public void setCurrentStepOrder(int currentStepOrder) {
        this.currentStepOrder = currentStepOrder;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public NuzlockeRules getRules() {
        return rules;
    }

    public void setRules(NuzlockeRules rules) {
        this.rules = rules;
    }

    public Set<String> getFlags() {
        return flags;
    }

    public void setFlags(Set<String> flags) {
        this.flags = flags;
    }

    public Set<String> getDefeatedTrainers() {
        return defeatedTrainers;
    }

    public void setDefeatedTrainers(Set<String> defeatedTrainers) {
        this.defeatedTrainers = defeatedTrainers;
    }

    public Set<String> getUnlocks() {
        return unlocks;
    }

    public void setUnlocks(Set<String> unlocks) {
        this.unlocks = unlocks;
    }

    public Set<String> getBadges() {
        return badges;
    }

    public void setBadges(Set<String> badges) {
        this.badges = badges;
    }

    


}
