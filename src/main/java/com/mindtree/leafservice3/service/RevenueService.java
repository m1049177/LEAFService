package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Revenue;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Revenue}.
 */
public interface RevenueService {

    /**
     * Save a revenue.
     *
     * @param revenue the entity to save.
     * @return the persisted entity.
     */
    Revenue save(Revenue revenue);

    /**
     * Get all the revenues.
     *
     * @return the list of entities.
     */
    List<Revenue> findAll();


    /**
     * Get the "id" revenue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Revenue> findOne(Long id);

    /**
     * Delete the "id" revenue.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the revenue corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Revenue> search(String query);
}
