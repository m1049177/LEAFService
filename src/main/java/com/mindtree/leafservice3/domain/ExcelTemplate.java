package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import com.mindtree.leafservice3.domain.enumeration.TemplateType;

/**
 * A ExcelTemplate.
 */
@Entity
@Table(name = "excel_template")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "exceltemplate", shards = 8)
public class ExcelTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    
    @Lob
    @Column(name = "file_name", nullable = false)
    private byte[] fileName;

    @Column(name = "file_name_content_type", nullable = false)
    private String fileNameContentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TemplateType type;

    @ManyToOne
    private Company company;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getFileName() {
        return fileName;
    }

    public ExcelTemplate fileName(byte[] fileName) {
        this.fileName = fileName;
        return this;
    }

    public void setFileName(byte[] fileName) {
        this.fileName = fileName;
    }

    public String getFileNameContentType() {
        return fileNameContentType;
    }

    public ExcelTemplate fileNameContentType(String fileNameContentType) {
        this.fileNameContentType = fileNameContentType;
        return this;
    }

    public void setFileNameContentType(String fileNameContentType) {
        this.fileNameContentType = fileNameContentType;
    }

    public TemplateType getType() {
        return type;
    }

    public ExcelTemplate type(TemplateType type) {
        this.type = type;
        return this;
    }

    public void setType(TemplateType type) {
        this.type = type;
    }

    public Company getCompany() {
        return company;
    }

    public ExcelTemplate company(Company company) {
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
        if (!(o instanceof ExcelTemplate)) {
            return false;
        }
        return id != null && id.equals(((ExcelTemplate) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ExcelTemplate{" +
            "id=" + getId() +
            ", fileName='" + getFileName() + "'" +
            ", fileNameContentType='" + getFileNameContentType() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
