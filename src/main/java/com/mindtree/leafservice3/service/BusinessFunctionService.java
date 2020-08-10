package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.BusinessFunction;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link BusinessFunction}.
 */
public interface BusinessFunctionService {

    /**
     * Save a businessFunction.
     *
     * @param businessFunction the entity to save.
     * @return the persisted entity.
     */
    BusinessFunction save(BusinessFunction businessFunction);

    /**
     * Get all the businessFunctions.
     *
     * @return the list of entities.
     */
    List<BusinessFunction> findAll();


    /**
     * Get the "id" businessFunction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BusinessFunction> findOne(Long id);

    /**
     * Delete the "id" businessFunction.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the businessFunction corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<BusinessFunction> search(String query);
}
