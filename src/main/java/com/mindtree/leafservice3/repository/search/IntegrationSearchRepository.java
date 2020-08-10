package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Integration;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Integration} entity.
 */
public interface IntegrationSearchRepository extends ElasticsearchRepository<Integration, Long> {
}
