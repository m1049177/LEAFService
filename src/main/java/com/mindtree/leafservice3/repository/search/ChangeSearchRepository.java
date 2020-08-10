package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Change;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Change} entity.
 */
public interface ChangeSearchRepository extends ElasticsearchRepository<Change, Long> {
}
