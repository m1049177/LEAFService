package com.mindtree.leafservice3.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;

import com.mindtree.leafservice3.domain.enumeration.ExpenditureSubType;

import com.mindtree.leafservice3.domain.enumeration.CurrencySuccessor;

/**
 * A Spend.
 */
@Entity
@Table(name = "spend")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "spend", shards = 8)
public class Spend implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "date_of_update", nullable = false)
    private LocalDate dateOfUpdate;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @NotNull
    @Column(name = "spend_id", nullable = false)
    private Long spendId;

    @NotNull
    @Column(name = "expenditure_type", nullable = false)
    private String expenditureType;

    @Column(name = "expenditure_sub_type")
    private String expenditureSubType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "successor", nullable = false)
    private CurrencySuccessor successor;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateOfUpdate() {
        return dateOfUpdate;
    }

    public Spend dateOfUpdate(LocalDate dateOfUpdate) {
        this.dateOfUpdate = dateOfUpdate;
        return this;
    }

    public void setDateOfUpdate(LocalDate dateOfUpdate) {
        this.dateOfUpdate = dateOfUpdate;
    }

    public Integer getAmount() {
        return amount;
    }

    public Spend amount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getSpendId() {
        return spendId;
    }

    public Spend spendId(Long spendId) {
        this.spendId = spendId;
        return this;
    }

    public void setSpendId(Long spendId) {
        this.spendId = spendId;
    }

    public String getExpenditureType() {
        return expenditureType;
    }

    public Spend expenditureType(String expenditureType) {
        this.expenditureType = expenditureType;
        return this;
    }

    public void setExpenditureType(String expenditureType) {
        this.expenditureType = expenditureType;
    }

    public String getExpenditureSubType() {
        return expenditureSubType;
    }

    public Spend expenditureSubType(String expenditureSubType) {
        this.expenditureSubType = expenditureSubType;
        return this;
    }

    public void setExpenditureSubType(String expenditureSubType) {
        this.expenditureSubType = expenditureSubType;
    }

    public CurrencySuccessor getSuccessor() {
        return successor;
    }

    public Spend successor(CurrencySuccessor successor) {
        this.successor = successor;
        return this;
    }

    public void setSuccessor(CurrencySuccessor successor) {
        this.successor = successor;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Spend)) {
            return false;
        }
        return id != null && id.equals(((Spend) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Spend{" +
            "id=" + getId() +
            ", dateOfUpdate='" + getDateOfUpdate() + "'" +
            ", amount=" + getAmount() +
            ", spendId=" + getSpendId() +
            ", expenditureType='" + getExpenditureType() + "'" +
            ", expenditureSubType='" + getExpenditureSubType() + "'" +
            ", successor='" + getSuccessor() + "'" +
            "}";
    }
}
