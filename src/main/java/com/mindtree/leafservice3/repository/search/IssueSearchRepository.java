package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Issue;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Issue} entity.
 */
public interface IssueSearchRepository extends ElasticsearchRepository<Issue, Long> {
}
