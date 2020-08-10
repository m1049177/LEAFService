package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

/**
 * A Functionality.
 */
@Entity
@Table(name = "functionality")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "functionality", shards = 8)
public class Functionality implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "major_modules", nullable = false)
    private String majorModules;

    @ManyToOne
    @JsonIgnoreProperties("functionalities")
    private Application application;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Functionality name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajorModules() {
        return majorModules;
    }

    public Functionality majorModules(String majorModules) {
        this.majorModules = majorModules;
        return this;
    }

    public void setMajorModules(String majorModules) {
        this.majorModules = majorModules;
    }

    public Application getApplication() {
        return application;
    }

    public Functionality application(Application application) {
        this.application = application;
        return this;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Functionality)) {
            return false;
        }
        return id != null && id.equals(((Functionality) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Functionality{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", majorModules='" + getMajorModules() + "'" +
            "}";
    }
}
