package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Capabilities;
import com.mindtree.leafservice3.service.dto.ChartData;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Capabilities}.
 */
public interface CapabilitiesService {

    /**
     * Save a capabilities.
     *
     * @param capabilities the entity to save.
     * @return the persisted entity.
     */
    Capabilities save(Capabilities capabilities);

    /**
     * Get all the capabilities.
     *
     * @return the list of entities.
     */
    List<Capabilities> findAll();


    /**
     * Get the "id" capabilities.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Capabilities> findOne(Long id);

    /**
     * Delete the "id" capabilities.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the capabilities corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Capabilities> search(String query);

    List<ChartData> organizationalChartData(Long company_id);

}
