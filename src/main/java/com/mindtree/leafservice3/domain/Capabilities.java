package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

/**
 * A Capabilities.
 */
@Entity
@Table(name = "capabilities")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "capabilities", shards = 8)
public class Capabilities implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JsonIgnoreProperties("capabilities")
    private BusinessFunction businessFunction;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public Capabilities description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BusinessFunction getBusinessFunction() {
        return businessFunction;
    }

    public Capabilities businessFunction(BusinessFunction businessFunction) {
        this.businessFunction = businessFunction;
        return this;
    }

    public void setBusinessFunction(BusinessFunction businessFunction) {
        this.businessFunction = businessFunction;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Capabilities)) {
            return false;
        }
        return id != null && id.equals(((Capabilities) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Capabilities{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
