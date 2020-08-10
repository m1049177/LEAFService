package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

/**
 * A Label.
 */
@Entity
@Table(name = "label")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "label", shards = 8)
public class Label implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "label_type", nullable = false)
    private String label_type;

    
    @Lob
    @Column(name = "label_data", nullable = false)
    private String label_data;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("labels")
    private Company company;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel_type() {
        return label_type;
    }

    public Label label_type(String label_type) {
        this.label_type = label_type;
        return this;
    }

    public void setLabel_type(String label_type) {
        this.label_type = label_type;
    }

    public String getLabel_data() {
        return label_data;
    }

    public Label label_data(String label_data) {
        this.label_data = label_data;
        return this;
    }

    public void setLabel_data(String label_data) {
        this.label_data = label_data;
    }

    public Company getCompany() {
        return company;
    }

    public Label company(Company company) {
        this.company = company;
        return this;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Label)) {
            return false;
        }
        return id != null && id.equals(((Label) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Label{" +
            "id=" + getId() +
            ", label_type='" + getLabel_type() + "'" +
            ", label_data='" + getLabel_data() + "'" +
            "}";
    }
}
