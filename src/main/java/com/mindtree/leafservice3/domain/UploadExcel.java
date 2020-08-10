package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import com.mindtree.leafservice3.domain.enumeration.UploadExcelType;

/**
 * A UploadExcel.
 */
@Entity
@Table(name = "upload_excel")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "uploadexcel", shards = 8)
public class UploadExcel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    
    @Lob
    @Column(name = "name", nullable = false)
    private byte[] name;

    @Column(name = "name_content_type", nullable = false)
    private String nameContentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private UploadExcelType type;

    @ManyToOne(optional = false)
    @NotNull
    private Company company;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getName() {
        return name;
    }

    public UploadExcel name(byte[] name) {
        this.name = name;
        return this;
    }

    public void setName(byte[] name) {
        this.name = name;
    }

    public String getNameContentType() {
        return nameContentType;
    }

    public UploadExcel nameContentType(String nameContentType) {
        this.nameContentType = nameContentType;
        return this;
    }

    public void setNameContentType(String nameContentType) {
        this.nameContentType = nameContentType;
    }

    public UploadExcelType getType() {
        return type;
    }

    public UploadExcel type(UploadExcelType type) {
        this.type = type;
        return this;
    }

    public void setType(UploadExcelType type) {
        this.type = type;
    }

    public Company getCompany() {
        return company;
    }

    public UploadExcel company(Company company) {
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
        if (!(o instanceof UploadExcel)) {
            return false;
        }
        return id != null && id.equals(((UploadExcel) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "UploadExcel{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", nameContentType='" + getNameContentType() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
