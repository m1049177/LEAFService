package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Brand;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Brand}.
 */
public interface BrandService {

    /**
     * Save a brand.
     *
     * @param brand the entity to save.
     * @return the persisted entity.
     */
    Brand save(Brand brand);

    /**
     * Get all the brands.
     *
     * @return the list of entities.
     */
    List<Brand> findAll();


    /**
     * Get the "id" brand.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Brand> findOne(Long id);

    /**
     * Delete the "id" brand.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the brand corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Brand> search(String query);
}
