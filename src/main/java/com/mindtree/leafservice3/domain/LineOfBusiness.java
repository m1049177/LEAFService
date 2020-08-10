package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

/**
 * A LineOfBusiness.
 */
@Entity
@Table(name = "line_of_business")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "lineofbusiness", shards = 8)
public class LineOfBusiness implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JsonIgnoreProperties("lineOfBusinesses")
    private OraganizationalUnit oraganizationalUnit;

    @ManyToOne
    @JsonIgnoreProperties("lineOfBusinesses")
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

    public LineOfBusiness name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OraganizationalUnit getOraganizationalUnit() {
        return oraganizationalUnit;
    }

    public LineOfBusiness oraganizationalUnit(OraganizationalUnit oraganizationalUnit) {
        this.oraganizationalUnit = oraganizationalUnit;
        return this;
    }

    public void setOraganizationalUnit(OraganizationalUnit oraganizationalUnit) {
        this.oraganizationalUnit = oraganizationalUnit;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LineOfBusiness employee(Employee employee) {
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
        if (!(o instanceof LineOfBusiness)) {
            return false;
        }
        return id != null && id.equals(((LineOfBusiness) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "LineOfBusiness{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
