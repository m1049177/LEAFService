package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

/**
 * A Technology.
 */
@Entity
@Table(name = "technology")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "technology", shards = 8)
public class Technology implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties("technologies")
    private Application application;

    @ManyToOne
    @JsonIgnoreProperties("technologies")
    private TechnologyStack technologyStack;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public Technology application(Application application) {
        this.application = application;
        return this;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public TechnologyStack getTechnologyStack() {
        return technologyStack;
    }

    public Technology technologyStack(TechnologyStack technologyStack) {
        this.technologyStack = technologyStack;
        return this;
    }

    public void setTechnologyStack(TechnologyStack technologyStack) {
        this.technologyStack = technologyStack;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Technology)) {
            return false;
        }
        return id != null && id.equals(((Technology) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Technology{" +
            "id=" + getId() +
            "}";
    }
}
