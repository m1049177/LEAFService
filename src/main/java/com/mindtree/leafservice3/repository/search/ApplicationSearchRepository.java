package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Application;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Application} entity.
 */
public interface ApplicationSearchRepository extends ElasticsearchRepository<Application, Long> {
}
