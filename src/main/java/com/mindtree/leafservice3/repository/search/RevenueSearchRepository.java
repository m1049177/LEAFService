package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Revenue;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Revenue} entity.
 */
public interface RevenueSearchRepository extends ElasticsearchRepository<Revenue, Long> {
}
