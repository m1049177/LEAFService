package com.mindtree.leafservice3.repository.search;
import com.mindtree.leafservice3.domain.Assessment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Assessment} entity.
 */
public interface AssessmentSearchRepository extends ElasticsearchRepository<Assessment, Long> {
}
