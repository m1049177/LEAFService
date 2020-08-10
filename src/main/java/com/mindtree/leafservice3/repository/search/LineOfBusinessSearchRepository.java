package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.LineOfBusiness;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link LineOfBusiness} entity.
 */
public interface LineOfBusinessSearchRepository extends ElasticsearchRepository<LineOfBusiness, Long> {
}
