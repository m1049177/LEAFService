package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.TechnologyStack;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link TechnologyStack} entity.
 */
public interface TechnologyStackSearchRepository extends ElasticsearchRepository<TechnologyStack, Long> {
}
