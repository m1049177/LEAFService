package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Example;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Example} entity.
 */
public interface ExampleSearchRepository extends ElasticsearchRepository<Example, Long> {
}
