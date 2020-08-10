package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Label;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Label} entity.
 */
public interface LabelSearchRepository extends ElasticsearchRepository<Label, Long> {
}
