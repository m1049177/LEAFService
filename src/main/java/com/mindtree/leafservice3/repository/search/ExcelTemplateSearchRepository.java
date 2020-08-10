package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.ExcelTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ExcelTemplate} entity.
 */
public interface ExcelTemplateSearchRepository extends ElasticsearchRepository<ExcelTemplate, Long> {
}
