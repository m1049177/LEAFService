package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Functionality;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Functionality} entity.
 */
public interface FunctionalitySearchRepository extends ElasticsearchRepository<Functionality, Long> {
}
