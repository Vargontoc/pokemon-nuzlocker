package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities;

import es.vargontoc.framework.persistence.BaseEntity;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.EncounterRecord;
import es.vargontoc.pokemon.nuzlocker.domain.models.nuzlocke.enums.EncounterOutcomeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "run_encounter")
public class RunEncounterJpaEntity extends BaseEntity<Long> {
    
    @Column(name = "run_id")
    private Long runId;

    @Column(name = "location_id")
    private String location;

    @Column(name = "specie")
    private String specie;

    @Column(name = "is_shiny")
    private boolean shiny;

    @Column(name= "outcome")
    @Enumerated(EnumType.STRING)
    private EncounterOutcomeType outcome;

    @Column(name = "consumes_zone")
    private boolean consumesZone;

    @Column(name = "step_order")
    private int stepOrder;


    public void of(EncounterRecord record) {
        this.setConsumesZone(record.consumeZone());
        this.setLocation(record.locationId());
        this.setOutcome(record.outcome());
        this.setRunId(record.runId());
        this.setShiny(record.shiny());
        this.setSpecie(record.specie());
        this.setStepOrder(record.stepOrder());
    }

    public Long getRunId() {
        return runId;
    }

    public void setRunId(Long runId) {
        this.runId = runId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSpecie() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie = specie;
    }

    public boolean isShiny() {
        return shiny;
    }

    public void setShiny(boolean shiny) {
        this.shiny = shiny;
    }

    public EncounterOutcomeType getOutcome() {
        return outcome;
    }

    public void setOutcome(EncounterOutcomeType outcome) {
        this.outcome = outcome;
    }

    public boolean isConsumesZone() {
        return consumesZone;
    }

    public void setConsumesZone(boolean consumesZone) {
        this.consumesZone = consumesZone;
    }

    public int getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }


    

}
