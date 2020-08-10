package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Issue;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Issue}.
 */
public interface IssueService {

    /**
     * Save a issue.
     *
     * @param issue the entity to save.
     * @return the persisted entity.
     */
    Issue save(Issue issue);

    /**
     * Get all the issues.
     *
     * @return the list of entities.
     */
    List<Issue> findAll();


    /**
     * Get the "id" issue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Issue> findOne(Long id);

    /**
     * Delete the "id" issue.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the issue corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Issue> search(String query);
}
