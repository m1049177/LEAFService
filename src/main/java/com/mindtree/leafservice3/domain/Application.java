package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;

import com.mindtree.leafservice3.domain.enumeration.ApplicationType;

import com.mindtree.leafservice3.domain.enumeration.ApplicationStatus;

/**
 * A Application.
 */
@Entity
@Table(name = "application")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "application", shards = 8)
public class Application implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ApplicationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ApplicationStatus status;

    @NotNull
    @Column(name = "implementation_date", nullable = false)
    private LocalDate implementationDate;

    @ManyToOne
    @JsonIgnoreProperties("applications")
    private LineOfBusiness lineOfBusiness;

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

    public Application name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Application description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ApplicationType getType() {
        return type;
    }

    public Application type(ApplicationType type) {
        this.type = type;
        return this;
    }

    public void setType(ApplicationType type) {
        this.type = type;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public Application status(ApplicationStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDate getImplementationDate() {
        return implementationDate;
    }

    public Application implementationDate(LocalDate implementationDate) {
        this.implementationDate = implementationDate;
        return this;
    }

    public void setImplementationDate(LocalDate implementationDate) {
        this.implementationDate = implementationDate;
    }

    public LineOfBusiness getLineOfBusiness() {
        return lineOfBusiness;
    }

    public Application lineOfBusiness(LineOfBusiness lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
        return this;
    }

    public void setLineOfBusiness(LineOfBusiness lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Application)) {
            return false;
        }
        return id != null && id.equals(((Application) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Application{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", type='" + getType() + "'" +
            ", status='" + getStatus() + "'" +
            ", implementationDate='" + getImplementationDate() + "'" +
            "}";
    }
}
