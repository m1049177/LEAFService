package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Change;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Change}.
 */
public interface ChangeService {

    /**
     * Save a change.
     *
     * @param change the entity to save.
     * @return the persisted entity.
     */
    Change save(Change change);

    /**
     * Get all the changes.
     *
     * @return the list of entities.
     */
    List<Change> findAll();


    /**
     * Get the "id" change.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Change> findOne(Long id);

    /**
     * Delete the "id" change.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the change corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Change> search(String query);
}
