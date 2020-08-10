package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.TechnologyStack;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link TechnologyStack}.
 */
public interface TechnologyStackService {

    /**
     * Save a technologyStack.
     *
     * @param technologyStack the entity to save.
     * @return the persisted entity.
     */
    TechnologyStack save(TechnologyStack technologyStack);

    /**
     * Get all the technologyStacks.
     *
     * @return the list of entities.
     */
    List<TechnologyStack> findAll();


    /**
     * Get the "id" technologyStack.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TechnologyStack> findOne(Long id);

    /**
     * Delete the "id" technologyStack.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the technologyStack corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<TechnologyStack> search(String query);
}
