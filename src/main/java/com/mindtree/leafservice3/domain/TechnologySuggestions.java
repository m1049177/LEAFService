package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import com.mindtree.leafservice3.domain.enumeration.Type;

/**
 * A TechnologySuggestions.
 */
@Entity
@Table(name = "technology_suggestions")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "technologysuggestions", shards = 8)
public class TechnologySuggestions implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @Column(name = "version")
    private String version;

    @Column(name = "description")
    private String description;

    @Column(name = "suggestion")
    private String suggestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("technologySuggestions")
    private TechnologyRecommendation technologyRecommendation;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public TechnologySuggestions type(Type type) {
        this.type = type;
        return this;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public TechnologySuggestions version(String version) {
        this.version = version;
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public TechnologySuggestions description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public TechnologySuggestions suggestion(String suggestion) {
        this.suggestion = suggestion;
        return this;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public TechnologyRecommendation getTechnologyRecommendation() {
        return technologyRecommendation;
    }

    public TechnologySuggestions technologyRecommendation(TechnologyRecommendation technologyRecommendation) {
        this.technologyRecommendation = technologyRecommendation;
        return this;
    }

    public void setTechnologyRecommendation(TechnologyRecommendation technologyRecommendation) {
        this.technologyRecommendation = technologyRecommendation;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TechnologySuggestions)) {
            return false;
        }
        return id != null && id.equals(((TechnologySuggestions) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TechnologySuggestions{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", version='" + getVersion() + "'" +
            ", description='" + getDescription() + "'" +
            ", suggestion='" + getSuggestion() + "'" +
            ",technologyRecommendation=''"+getTechnologyRecommendation()+"''"+
            "}";
    }
}
