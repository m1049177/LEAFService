package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Spend;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Spend} entity.
 */
public interface SpendSearchRepository extends ElasticsearchRepository<Spend, Long> {
}
