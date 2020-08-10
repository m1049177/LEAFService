package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Expenditure;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Expenditure} entity.
 */
public interface ExpenditureSearchRepository extends ElasticsearchRepository<Expenditure, Long> {
}
