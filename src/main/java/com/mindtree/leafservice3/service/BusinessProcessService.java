package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.BusinessProcess;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link BusinessProcess}.
 */
public interface BusinessProcessService {

    /**
     * Save a businessProcess.
     *
     * @param businessProcess the entity to save.
     * @return the persisted entity.
     */
    BusinessProcess save(BusinessProcess businessProcess);

    /**
     * Get all the businessProcesses.
     *
     * @return the list of entities.
     */
    List<BusinessProcess> findAll();


    /**
     * Get the "id" businessProcess.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BusinessProcess> findOne(Long id);

    /**
     * Delete the "id" businessProcess.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the businessProcess corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<BusinessProcess> search(String query);
}
