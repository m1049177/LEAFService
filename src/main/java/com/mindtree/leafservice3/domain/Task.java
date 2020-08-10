package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import com.mindtree.leafservice3.domain.enumeration.CurrencySuccessor;

/**
 * A Task.
 */
@Entity
@Table(name = "task")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "task", shards = 8)
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "estimated_cost", nullable = false)
    private Integer estimatedCost;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "successor", nullable = false)
    private CurrencySuccessor successor;

    @ManyToOne
    @JsonIgnoreProperties("tasks")
    private Activity activity;

    @ManyToOne
    @JsonIgnoreProperties("tasks")
    private Employee employee;

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

    public Task name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getEstimatedCost() {
        return estimatedCost;
    }

    public Task estimatedCost(Integer estimatedCost) {
        this.estimatedCost = estimatedCost;
        return this;
    }

    public void setEstimatedCost(Integer estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public CurrencySuccessor getSuccessor() {
        return successor;
    }

    public Task successor(CurrencySuccessor successor) {
        this.successor = successor;
        return this;
    }

    public void setSuccessor(CurrencySuccessor successor) {
        this.successor = successor;
    }

    public Activity getActivity() {
        return activity;
    }

    public Task activity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Task employee(Employee employee) {
        this.employee = employee;
        return this;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        return id != null && id.equals(((Task) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Task{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", estimatedCost=" + getEstimatedCost() +
            ", successor='" + getSuccessor() + "'" +
            "}";
    }
}
