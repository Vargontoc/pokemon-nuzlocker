package es.vargontoc.pokemon.nuzlocker.infrastructure.adapters.out.persistence.entities;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import es.vargontoc.framework.persistence.BaseEntity;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.BaseStats;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.LearnEntry;
import es.vargontoc.pokemon.nuzlocker.domain.models.pokedex.Specie;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pokedex_specie")
public class SpecieJpaEntity extends BaseEntity<Long>{
    
    @Column(name = "slug")
    private String slug;

    @Column(name = "name")
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> types;

    @JdbcTypeCode(SqlTypes.JSON)
    private BaseStats stats;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> abilities;

    @Column(name = "family")
    private String family;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<LearnEntry> learnset;


    public void of(Specie dto) {
        this.setId(dto.id());
        this.setSlug(dto.slug());
        this.setName(dto.name());

        this.setTypes(dto.types());
        this.setStats(dto.stats());
        this.setAbilities(dto.abilities());
        this.setFamily(dto.family());
        this.setLearnset(dto.learnSet());
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

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public BaseStats getStats() {
        return stats;
    }

    public void setStats(BaseStats stats) {
        this.stats = stats;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public List<LearnEntry> getLearnset() {
        return learnset;
    }

    public void setLearnset(List<LearnEntry> learnset) {
        this.learnset = learnset;
    }

    
}
