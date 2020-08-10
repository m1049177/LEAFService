package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Evaluation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Evaluation} entity.
 */
public interface EvaluationSearchRepository extends ElasticsearchRepository<Evaluation, Long> {
}
