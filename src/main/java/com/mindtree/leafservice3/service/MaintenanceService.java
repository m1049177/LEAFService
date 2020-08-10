package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Maintenance;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Maintenance}.
 */
public interface MaintenanceService {

    /**
     * Save a maintenance.
     *
     * @param maintenance the entity to save.
     * @return the persisted entity.
     */
    Maintenance save(Maintenance maintenance);

    /**
     * Get all the maintenances.
     *
     * @return the list of entities.
     */
    List<Maintenance> findAll();


    /**
     * Get the "id" maintenance.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Maintenance> findOne(Long id);

    /**
     * Delete the "id" maintenance.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the maintenance corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Maintenance> search(String query);
}
