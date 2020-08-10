package com.mindtree.leafservice3.repository.search;

import com.mindtree.leafservice3.domain.OraganizationalUnit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link OraganizationalUnit} entity.
 */
public interface OraganizationalUnitSearchRepository extends ElasticsearchRepository<OraganizationalUnit, Long> {
}
