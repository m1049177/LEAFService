package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Functionality;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Functionality}.
 */
public interface FunctionalityService {

    /**
     * Save a functionality.
     *
     * @param functionality the entity to save.
     * @return the persisted entity.
     */
    Functionality save(Functionality functionality);

    /**
     * Get all the functionalities.
     *
     * @return the list of entities.
     */
    List<Functionality> findAll();


    /**
     * Get the "id" functionality.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Functionality> findOne(Long id);

    /**
     * Delete the "id" functionality.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the functionality corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Functionality> search(String query);
}
