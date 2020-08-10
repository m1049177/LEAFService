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
 * A Budget.
 */
@Entity
@Table(name = "budget")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "budget", shards = 8)
public class Budget implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @NotNull
    @Column(name = "year", nullable = false)
    private Integer year;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "successor", nullable = false)
    private CurrencySuccessor successor;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("budgets")
    private Application application;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public Budget amount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getYear() {
        return year;
    }

    public Budget year(Integer year) {
        this.year = year;
        return this;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public CurrencySuccessor getSuccessor() {
        return successor;
    }

    public Budget successor(CurrencySuccessor successor) {
        this.successor = successor;
        return this;
    }

    public void setSuccessor(CurrencySuccessor successor) {
        this.successor = successor;
    }

    public Application getApplication() {
        return application;
    }

    public Budget application(Application application) {
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
        if (!(o instanceof Budget)) {
            return false;
        }
        return id != null && id.equals(((Budget) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Budget{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", year=" + getYear() +
            ", successor='" + getSuccessor() + "'" +
            "}";
    }
}
