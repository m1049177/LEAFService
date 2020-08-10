package com.mindtree.leafservice3.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

import com.mindtree.leafservice3.domain.enumeration.IntegrationFlowType;

/**
 * A Integration.
 */
@Entity
@Table(name = "integration")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "integration")
public class Integration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "flow_type", nullable = false)
    private IntegrationFlowType flowType;

    @NotNull
    @Column(name = "entity", nullable = false)
    private String entity;

    @ManyToOne
    @JsonIgnoreProperties("integrations")
    private Application application;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("integrations")
    private Application integrationApp;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IntegrationFlowType getFlowType() {
        return flowType;
    }

    public Integration flowType(IntegrationFlowType flowType) {
        this.flowType = flowType;
        return this;
    }

    public void setFlowType(IntegrationFlowType flowType) {
        this.flowType = flowType;
    }

    public String getEntity() {
        return entity;
    }

    public Integration entity(String entity) {
        this.entity = entity;
        return this;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Application getApplication() {
        return application;
    }

    public Integration application(Application application) {
        this.application = application;
        return this;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Application getIntegrationApp() {
        return integrationApp;
    }

    public Integration integrationApp(Application application) {
        this.integrationApp = application;
        return this;
    }

    public void setIntegrationApp(Application application) {
        this.integrationApp = application;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Integration)) {
            return false;
        }
        return id != null && id.equals(((Integration) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Integration{" +
            "id=" + getId() +
            ", flowType='" + getFlowType() + "'" +
            ", entity='" + getEntity() + "'" +
            "}";
    }
}
