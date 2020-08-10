package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Report;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Report} entity.
 */
public interface ReportSearchRepository extends ElasticsearchRepository<Report, Long> {
}
