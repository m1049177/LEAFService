package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.BusinessFunction;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link BusinessFunction} entity.
 */
public interface BusinessFunctionSearchRepository extends ElasticsearchRepository<BusinessFunction, Long> {
}
