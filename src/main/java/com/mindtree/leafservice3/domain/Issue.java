package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;

import com.mindtree.leafservice3.domain.enumeration.IssueStatus;

import com.mindtree.leafservice3.domain.enumeration.TypeOfIssue;

/**
 * A Issue.
 */
@Entity
@Table(name = "issue")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "issue", shards = 8)
public class Issue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "date_of_issue", nullable = false)
    private LocalDate dateOfIssue;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IssueStatus status;

    @Column(name = "solved_date", nullable = false)
    private LocalDate solvedDate;

    @Column(name = "solved_by")
    private String solvedBy;

    @Column(name = "number_of_days")
    private Integer numberOfDays;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_of_issue", nullable = false)
    private TypeOfIssue typeOfIssue;

    @NotNull
    @ManyToOne
    @JsonIgnoreProperties("issues")
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

    public Issue description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateOfIssue() {
        return dateOfIssue;
    }

    public Issue dateOfIssue(LocalDate dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
        return this;
    }

    public void setDateOfIssue(LocalDate dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public Issue status(IssueStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public LocalDate getSolvedDate() {
        return solvedDate;
    }

    public Issue solvedDate(LocalDate solvedDate) {
        this.solvedDate = solvedDate;
        return this;
    }

    public void setSolvedDate(LocalDate solvedDate) {
        this.solvedDate = solvedDate;
    }

    public String getSolvedBy() {
        return solvedBy;
    }

    public Issue solvedBy(String solvedBy) {
        this.solvedBy = solvedBy;
        return this;
    }

    public void setSolvedBy(String solvedBy) {
        this.solvedBy = solvedBy;
    }

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    public Issue numberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
        return this;
    }

    public void setNumberOfDays(Integer numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public TypeOfIssue getTypeOfIssue() {
        return typeOfIssue;
    }

    public Issue typeOfIssue(TypeOfIssue typeOfIssue) {
        this.typeOfIssue = typeOfIssue;
        return this;
    }

    public void setTypeOfIssue(TypeOfIssue typeOfIssue) {
        this.typeOfIssue = typeOfIssue;
    }

    public Application getApplication() {
        return application;
    }

    public Issue application(Application application) {
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
        if (!(o instanceof Issue)) {
            return false;
        }
        return id != null && id.equals(((Issue) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Issue{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", dateOfIssue='" + getDateOfIssue() + "'" +
            ", status='" + getStatus() + "'" +
            ", solvedDate='" + getSolvedDate() + "'" +
            ", solvedBy='" + getSolvedBy() + "'" +
            ", numberOfDays=" + getNumberOfDays() +
            ", typeOfIssue='" + getTypeOfIssue() + "'" +
            "}";
    }
}
