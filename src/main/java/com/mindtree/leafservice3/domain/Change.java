package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Change.
 */
@Entity
@Table(name = "jhi_change")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "change", shards = 8)
public class Change implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "date_of_change", nullable = false)
    private LocalDate dateOfChange;

    @ManyToOne
    @JsonIgnoreProperties("changes")
    private Application application;

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

    public Change description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateOfChange() {
        return dateOfChange;
    }

    public Change dateOfChange(LocalDate dateOfChange) {
        this.dateOfChange = dateOfChange;
        return this;
    }

    public void setDateOfChange(LocalDate dateOfChange) {
        this.dateOfChange = dateOfChange;
    }

    public Application getApplication() {
        return application;
    }

    public Change application(Application application) {
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
        if (!(o instanceof Change)) {
            return false;
        }
        return id != null && id.equals(((Change) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Change{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", dateOfChange='" + getDateOfChange() + "'" +
            "}";
    }
}
