package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Company;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Company}.
 */
public interface CompanyService {

    /**
     * Save a company.
     *
     * @param company the entity to save.
     * @return the persisted entity.
     */
    Company save(Company company);

    /**
     * Get all the companies.
     *
     * @return the list of entities.
     */
    List<Company> findAll();


    /**
     * Get the "id" company.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Company> findOne(Long id);

    /**
     * Delete the "id" company.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the company corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Company> search(String query);
}
