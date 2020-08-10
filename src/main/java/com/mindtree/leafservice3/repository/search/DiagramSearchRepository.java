package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Diagram;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Diagram} entity.
 */
public interface DiagramSearchRepository extends ElasticsearchRepository<Diagram, Long> {
}
