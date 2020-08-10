package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Budget;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Budget} entity.
 */
public interface BudgetSearchRepository extends ElasticsearchRepository<Budget, Long> {
}
