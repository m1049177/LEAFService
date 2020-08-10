package com.mindtree.leafservice3.repository.search;
import com.mindtree.leafservice3.domain.TechnologyRecommendation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link TechnologyRecommendation} entity.
 */
public interface TechnologyRecommendationSearchRepository extends ElasticsearchRepository<TechnologyRecommendation, Long> {
}
