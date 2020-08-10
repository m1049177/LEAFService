package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.OraganizationalUnit;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link OraganizationalUnit}.
 */
public interface OraganizationalUnitService {

    /**
     * Save a oraganizationalUnit.
     *
     * @param oraganizationalUnit the entity to save.
     * @return the persisted entity.
     */
    OraganizationalUnit save(OraganizationalUnit oraganizationalUnit);

    /**
     * Get all the oraganizationalUnits.
     *
     * @return the list of entities.
     */
    List<OraganizationalUnit> findAll();


    /**
     * Get the "id" oraganizationalUnit.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OraganizationalUnit> findOne(Long id);

    /**
     * Delete the "id" oraganizationalUnit.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the oraganizationalUnit corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<OraganizationalUnit> search(String query);
}
