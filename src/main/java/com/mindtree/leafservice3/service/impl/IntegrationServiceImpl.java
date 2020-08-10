package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.IntegrationService;
import com.mindtree.leafservice3.service.dto.IntegrationData;
import com.mindtree.leafservice3.service.dto.Links;
import com.mindtree.leafservice3.domain.Application;
import com.mindtree.leafservice3.domain.Integration;
import com.mindtree.leafservice3.repository.ApplicationRepository;
import com.mindtree.leafservice3.repository.IntegrationRepository;
import com.mindtree.leafservice3.repository.search.IntegrationSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Integration}.
 */
@Service
@Transactional
public class IntegrationServiceImpl implements IntegrationService {

    private final Logger log = LoggerFactory.getLogger(IntegrationServiceImpl.class);

    private final IntegrationRepository integrationRepository;
    private final ApplicationRepository applicationRepository;

    private final IntegrationSearchRepository integrationSearchRepository;

    public IntegrationServiceImpl(IntegrationRepository integrationRepository,
            IntegrationSearchRepository integrationSearchRepository,
            ApplicationRepository applicationRepository) {
        this.integrationRepository = integrationRepository;
        this.integrationSearchRepository = integrationSearchRepository;
        this.applicationRepository = applicationRepository;
    }

    /**
     * Save a integration.
     *
     * @param integration the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Integration save(Integration integration) {
        log.debug("Request to save Integration : {}", integration);
        Integration result = integrationRepository.save(integration);
        integrationSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the integrations.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Integration> findAll() {
        log.debug("Request to get all Integrations");
        return integrationRepository.findAll();
    }

    /**
     * Get one integration by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Integration> findOne(Long id) {
        log.debug("Request to get Integration : {}", id);
        return integrationRepository.findById(id);
    }

    /**
     * Delete the integration by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Integration : {}", id);
        integrationRepository.deleteById(id);
        integrationSearchRepository.deleteById(id);
    }

    /**
     * Search for the integration corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Integration> search(String query) {
        log.debug("Request to search Integrations for query {}", query);
        return StreamSupport.stream(integrationSearchRepository.search(queryStringQuery(query)).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public IntegrationData getIntegrationData(Long app_id) {
        Optional<Application> application = applicationRepository.findById(app_id);
        List<Integration> data = integrationRepository.findAll();
        IntegrationData integration = new IntegrationData();
        List<Links> linkData = new ArrayList<>();

        integration.mainAppId = application.get().getId();
        integration.mainAppName = application.get().getName();
        

        for (Integration item : data) {
            if(item.getApplication().getId() == app_id) {
                Links linkObject = new Links();
                linkObject.appId = item.getIntegrationApp().getId();
                linkObject.appName = item.getIntegrationApp().getName();
                linkObject.entity = item.getEntity();
                linkObject.flowType = item.getFlowType();
                linkData.add(linkObject);
                // integration.links.add(linkObject);
            }
        }
        integration.links = linkData;
       return integration; 
    }
}
