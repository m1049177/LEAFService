package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import com.mindtree.leafservice3.domain.enumeration.BusinessFunctionType;

/**
 * A BusinessFunction.
 */
@Entity
@Table(name = "business_function")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "businessfunction", shards = 8)
public class BusinessFunction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BusinessFunctionType type;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JsonIgnoreProperties("businessFunctions")
    private LineOfBusiness lineOfBusiness;

    @ManyToOne
    @JsonIgnoreProperties("businessFunctions")
    private Employee employee;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BusinessFunctionType getType() {
        return type;
    }

    public BusinessFunction type(BusinessFunctionType type) {
        this.type = type;
        return this;
    }

    public void setType(BusinessFunctionType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public BusinessFunction name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LineOfBusiness getLineOfBusiness() {
        return lineOfBusiness;
    }

    public BusinessFunction lineOfBusiness(LineOfBusiness lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
        return this;
    }

    public void setLineOfBusiness(LineOfBusiness lineOfBusiness) {
        this.lineOfBusiness = lineOfBusiness;
    }

    public Employee getEmployee() {
        return employee;
    }

    public BusinessFunction employee(Employee employee) {
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
        if (!(o instanceof BusinessFunction)) {
            return false;
        }
        return id != null && id.equals(((BusinessFunction) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "BusinessFunction{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
