package com.mindtree.leafservice3.repository.search;
import com.mindtree.leafservice3.domain.TechnologySuggestions;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link TechnologySuggestions} entity.
 */
public interface TechnologySuggestionsSearchRepository extends ElasticsearchRepository<TechnologySuggestions, Long> {
}
