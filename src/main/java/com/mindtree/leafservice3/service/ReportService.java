package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Report;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Report}.
 */
public interface ReportService {

    /**
     * Save a report.
     *
     * @param report the entity to save.
     * @return the persisted entity.
     */
    Report save(Report report);

    /**
     * Get all the reports.
     *
     * @return the list of entities.
     */
    List<Report> findAll();


    /**
     * Get the "id" report.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Report> findOne(Long id);

    /**
     * Delete the "id" report.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the report corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Report> search(String query);

    void pdfGenerator();
}
