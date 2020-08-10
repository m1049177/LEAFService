package com.mindtree.leafservice3.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;


/**
 * A TechnologyRecommendation.
 */
@Entity
@Table(name = "technology_recommendation")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "technologyrecommendation", shards = 8)
public class TechnologyRecommendation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @NotNull
    @Column(name = "technology_name", nullable = false)
    private String technologyName;

    @Column(name = "outdated_version")
    private String outdatedVersion;

    @NotNull
    @Column(name = "stable_version", nullable = false)
    private String stableVersion;

    @Column(name = "latest_version")
    private String latestVersion;

    @Column(name = "new_features")
    private String newFeatures;

    @Column(name = "technology_type")
    private String technologyType;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTechnologyName() {
        return technologyName;
    }

    public TechnologyRecommendation technologyName(String technologyName) {
        this.technologyName = technologyName;
        return this;
    }

    public void setTechnologyName(String technologyName) {
        this.technologyName = technologyName;
    }

    public String getOutdatedVersion() {
        return outdatedVersion;
    }

    public TechnologyRecommendation outdatedVersion(String outdatedVersion) {
        this.outdatedVersion = outdatedVersion;
        return this;
    }

    public void setOutdatedVersion(String outdatedVersion) {
        this.outdatedVersion = outdatedVersion;
    }

    public String getStableVersion() {
        return stableVersion;
    }

    public TechnologyRecommendation stableVersion(String stableVersion) {
        this.stableVersion = stableVersion;
        return this;
    }

    public void setStableVersion(String stableVersion) {
        this.stableVersion = stableVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public TechnologyRecommendation latestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
        return this;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getNewFeatures() {
        return newFeatures;
    }

    public TechnologyRecommendation newFeatures(String newFeatures) {
        this.newFeatures = newFeatures;
        return this;
    }

    public void setNewFeatures(String newFeatures) {
        this.newFeatures = newFeatures;
    }

    public String getTechnologyType() {
        return technologyType;
    }

    public TechnologyRecommendation technologyType(String technologyType) {
        this.technologyType = technologyType;
        return this;
    }

    public void setTechnologyType(String technologyType) {
        this.technologyType = technologyType;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TechnologyRecommendation)) {
            return false;
        }
        return id != null && id.equals(((TechnologyRecommendation) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "TechnologyRecommendation{" +
            "id=" + getId() +
            ", technologyName='" + getTechnologyName() + "'" +
            ", outdatedVersion='" + getOutdatedVersion() + "'" +
            ", stableVersion='" + getStableVersion() + "'" +
            ", latestVersion='" + getLatestVersion() + "'" +
            ", newFeatures='" + getNewFeatures() + "'" +
            ", technologyType='" + getTechnologyType() + "'" +
            "}";
    }
}
