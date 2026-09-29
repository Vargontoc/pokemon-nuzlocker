package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities;

import es.vargontoc.framework.persistence.BaseEntity;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Ability;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pokedex_ability")
public class AbilityJpaEntity extends BaseEntity<Long>{
    
    @Column(name = "slug")
    private String slug;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    public void of(Ability dto){
        this.setId(dto.id());
        this.setSlug(dto.slug());
        this.setName(dto.name());
        this.setDescription(dto.description());
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

    
}
