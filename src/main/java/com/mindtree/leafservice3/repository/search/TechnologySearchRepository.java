package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Technology;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Technology} entity.
 */
public interface TechnologySearchRepository extends ElasticsearchRepository<Technology, Long> {
}
