package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.BusinessProcess;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link BusinessProcess} entity.
 */
public interface BusinessProcessSearchRepository extends ElasticsearchRepository<BusinessProcess, Long> {
}
