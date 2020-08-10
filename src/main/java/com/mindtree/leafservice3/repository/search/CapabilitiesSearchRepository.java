package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Capabilities;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Capabilities} entity.
 */
public interface CapabilitiesSearchRepository extends ElasticsearchRepository<Capabilities, Long> {
}
