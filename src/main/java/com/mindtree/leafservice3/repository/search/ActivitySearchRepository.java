package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Activity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Activity} entity.
 */
public interface ActivitySearchRepository extends ElasticsearchRepository<Activity, Long> {
}
