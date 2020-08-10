package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.LineOfBusiness;
import com.mindtree.leafservice3.service.dto.LobSearchData;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link LineOfBusiness}.
 */
public interface LineOfBusinessService {

    /**
     * Save a lineOfBusiness.
     *
     * @param lineOfBusiness the entity to save.
     * @return the persisted entity.
     */
    LineOfBusiness save(LineOfBusiness lineOfBusiness);

    /**
     * Get all the lineOfBusinesses.
     *
     * @return the list of entities.
     */
    List<LineOfBusiness> findAll();


    /**
     * Get the "id" lineOfBusiness.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LineOfBusiness> findOne(Long id);

    /**
     * Delete the "id" lineOfBusiness.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the lineOfBusiness corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<LineOfBusiness> search(String query);

    LobSearchData searchData(Long lob_id);
}
