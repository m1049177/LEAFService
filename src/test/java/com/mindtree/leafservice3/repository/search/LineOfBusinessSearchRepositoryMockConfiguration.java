package com.mindtree.leafservice3.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link LineOfBusinessSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class LineOfBusinessSearchRepositoryMockConfiguration {

    @MockBean
    private LineOfBusinessSearchRepository mockLineOfBusinessSearchRepository;

}
