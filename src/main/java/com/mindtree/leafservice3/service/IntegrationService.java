package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Integration;
import com.mindtree.leafservice3.service.dto.IntegrationData;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Integration}.
 */
public interface IntegrationService {

    /**
     * Save a integration.
     *
     * @param integration the entity to save.
     * @return the persisted entity.
     */
    Integration save(Integration integration);

    /**
     * Get all the integrations.
     *
     * @return the list of entities.
     */
    List<Integration> findAll();


    /**
     * Get the "id" integration.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Integration> findOne(Long id);

    /**
     * Delete the "id" integration.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the integration corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Integration> search(String query);

    IntegrationData getIntegrationData(Long app_id);
}
