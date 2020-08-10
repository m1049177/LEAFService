package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Spend;
import com.mindtree.leafservice3.service.dto.ApplicationData;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Spend}.
 */
public interface SpendService {

    /**
     * Save a spend.
     *
     * @param spend the entity to save.
     * @return the persisted entity.
     */
    Spend save(Spend spend);

    /**
     * Get all the spends.
     *
     * @return the list of entities.
     */
    List<Spend> findAll();


    /**
     * Get the "id" spend.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Spend> findOne(Long id);

    /**
     * Delete the "id" spend.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the spend corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Spend> search(String query);

    List<ApplicationData> getApplicationData(Long company_id);

    List<Spend> getSpendData(Long company_id);

    List<Object> findYearlySpendDetails(Long company_id);

}
