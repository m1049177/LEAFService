package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Expenditure;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Expenditure}.
 */
public interface ExpenditureService {

    /**
     * Save a expenditure.
     *
     * @param expenditure the entity to save.
     * @return the persisted entity.
     */
    Expenditure save(Expenditure expenditure);

    /**
     * Get all the expenditures.
     *
     * @return the list of entities.
     */
    List<Expenditure> findAll();


    /**
     * Get the "id" expenditure.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Expenditure> findOne(Long id);

    /**
     * Delete the "id" expenditure.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the expenditure corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Expenditure> search(String query);
}
