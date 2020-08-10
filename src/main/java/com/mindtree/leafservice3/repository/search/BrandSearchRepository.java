package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.Brand;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Brand} entity.
 */
public interface BrandSearchRepository extends ElasticsearchRepository<Brand, Long> {
}
