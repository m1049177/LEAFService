package com.mindtree.leafservice3.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import com.mindtree.leafservice3.domain.enumeration.AssessmentCategory;

/**
 * A Assessment.
 */
@Entity
@Table(name = "assessment")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "assessment", shards = 8)
public class Assessment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_category")
    private AssessmentCategory assessmentCategory;

    @Lob
    @Column(name = "questions")
    private String questions;

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

    public Assessment assessmentCategory(AssessmentCategory assessmentCategory) {
        this.assessmentCategory = assessmentCategory;
        return this;
    }

    public void setAssessmentCategory(AssessmentCategory assessmentCategory) {
        this.assessmentCategory = assessmentCategory;
    }

    public String getQuestions() {
        return questions;
    }

    public Assessment questions(String questions) {
        this.questions = questions;
        return this;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Assessment)) {
            return false;
        }
        return id != null && id.equals(((Assessment) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Assessment{" +
            "id=" + getId() +
            ", assessmentCategory='" + getAssessmentCategory() + "'" +
            ", questions='" + getQuestions() + "'" +
            "}";
    }
}
