package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Maintenance;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Maintenance} entity.
 */
public interface MaintenanceSearchRepository extends ElasticsearchRepository<Maintenance, Long> {
}
