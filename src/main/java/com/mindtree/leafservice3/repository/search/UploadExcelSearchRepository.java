package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.UploadExcel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link UploadExcel} entity.
 */
public interface UploadExcelSearchRepository extends ElasticsearchRepository<UploadExcel, Long> {
}
