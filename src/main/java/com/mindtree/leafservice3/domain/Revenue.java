package com.mindtree.leafservice3.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mindtree.leafservice3.domain.enumeration.CurrencySuccessor;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;


/**
 * A Revenue.
 */
@Entity
@Table(name = "revenue")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "revenue", shards = 8)
public class Revenue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "amount")
    private Integer amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "successor", nullable = false)
    private CurrencySuccessor successor;

    @ManyToOne
    @JsonIgnoreProperties("revenues")
    private LineOfBusiness lineOfBusiness;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Revenue date(LocalDate date) {
        this.date = date;
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getAmount() {
        return amount;
    }

    public Revenue amount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public CurrencySuccessor getSuccessor() {
        return successor;
    }

    public Revenue successor(CurrencySuccessor successor) {
        this.successor = successor;
        return this;
    }

    public void setSuccessor(CurrencySuccessor successor) {
        this.successor = successor;
    }

    public LineOfBusiness getLineOfBusiness() {
        return lineOfBusiness;
    }

    public Revenue lineOfBusiness(LineOfBusiness lineOfBusiness) {
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
        if (!(o instanceof Revenue)) {
            return false;
        }
        return id != null && id.equals(((Revenue) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Revenue{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", amount=" + getAmount() +
            ", successor='" + getSuccessor() + "'" +
            "}";
    }
}
