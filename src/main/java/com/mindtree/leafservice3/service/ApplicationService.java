package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Application;
import com.mindtree.leafservice3.service.dto.ChartData;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Application}.
 */
public interface ApplicationService {

    /**
     * Save a application.
     *
     * @param application the entity to save.
     * @return the persisted entity.
     */
    Application save(Application application);

    /**
     * Get all the applications.
     *
     * @return the list of entities.
     */
    List<Application> findAll();


    /**
     * Get the "id" application.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Application> findOne(Long id);

    /**
     * Delete the "id" application.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the application corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Application> search(String query);

    List<ChartData> organizationalChartData();

    List<ChartData> getAppChartData(Long company_id);
}
