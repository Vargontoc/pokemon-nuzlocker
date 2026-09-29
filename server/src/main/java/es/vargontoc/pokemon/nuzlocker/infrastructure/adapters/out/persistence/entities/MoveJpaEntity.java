package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities;

import es.vargontoc.framework.persistence.BaseEntity;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Move;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.enums.CategoryMoveType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "pokedex_move")
public class MoveJpaEntity extends BaseEntity<Long> {
    
    @Column(name = "slug")
    private String slug;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    CategoryMoveType category;
    
    @Column(name = "power")
    private Integer power;

    @Column(name = "accuracy")
    private Integer accuracy;

    @Column(name = "pp")
    private Integer pp;
    
    @Column(name = "priority")
    private int priority;

    @Column(name = "effect_chance")
    private Integer effectChance;


    public void of(Move dto){
        this.setId(dto.id());
        this.setSlug(dto.slug());
        this.setName(dto.name());
        this.setDescription(dto.description());
        this.setType(dto.type());
        this.setPower(dto.power());
        this.setAccuracy(dto.accuracry());
        this.setPp(dto.pp());
        this.setEffectChance(dto.effectChance());
        this.setCategory(dto.category());
        this.setPriority(dto.priority());
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public Integer getPp() {
        return pp;
    }

    public void setPp(Integer pp) {
        this.pp = pp;
    }

    public Integer getEffectChance() {
        return effectChance;
    }

    public void setEffectChance(Integer effectChance) {
        this.effectChance = effectChance;
    }

    public CategoryMoveType getCategory() {
        return category;
    }

    public void setCategory(CategoryMoveType category) {
        this.category = category;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    
}
