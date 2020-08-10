package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;

import com.mindtree.leafservice3.domain.enumeration.AssessmentCategory;

/**
 * A Evaluation.
 */
@Entity
@Table(name = "evaluation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "evaluation", shards = 8)
public class Evaluation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_category", nullable = false)
    private AssessmentCategory assessmentCategory;

    @NotNull
    @Column(name = "score", nullable = false)
    private Integer score;

    
    @Lob
    @Column(name = "assessment_result", nullable = false)
    private String assessmentResult;

    @NotNull
    @Column(name = "attempt_date", nullable = false)
    private LocalDate attemptDate;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("evaluations")
    private Application application;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssessmentCategory getAssessmentCategory() {
        return assessmentCategory;
    }

    public Evaluation assessmentCategory(AssessmentCategory assessmentCategory) {
        this.assessmentCategory = assessmentCategory;
        return this;
    }

    public void setAssessmentCategory(AssessmentCategory assessmentCategory) {
        this.assessmentCategory = assessmentCategory;
    }

    public Integer getScore() {
        return score;
    }

    public Evaluation score(Integer score) {
        this.score = score;
        return this;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getAssessmentResult() {
        return assessmentResult;
    }

    public Evaluation assessmentResult(String assessmentResult) {
        this.assessmentResult = assessmentResult;
        return this;
    }

    public void setAssessmentResult(String assessmentResult) {
        this.assessmentResult = assessmentResult;
    }

    public LocalDate getAttemptDate() {
        return attemptDate;
    }

    public Evaluation attemptDate(LocalDate attemptDate) {
        this.attemptDate = attemptDate;
        return this;
    }

    public void setAttemptDate(LocalDate attemptDate) {
        this.attemptDate = attemptDate;
    }

    public Application getApplication() {
        return application;
    }

    public Evaluation application(Application application) {
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
        if (!(o instanceof Evaluation)) {
            return false;
        }
        return id != null && id.equals(((Evaluation) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
            "id=" + getId() +
            ", assessmentCategory='" + getAssessmentCategory() + "'" +
            ", score=" + getScore() +
            ", assessmentResult='" + getAssessmentResult() + "'" +
            ", attemptDate='" + getAttemptDate() + "'" +
            "}";
    }
}
