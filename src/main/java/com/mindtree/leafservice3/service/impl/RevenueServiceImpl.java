package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.RevenueService;
import com.mindtree.leafservice3.domain.Revenue;
import com.mindtree.leafservice3.repository.RevenueRepository;
import com.mindtree.leafservice3.repository.search.RevenueSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Revenue}.
 */
@Service
@Transactional
public class RevenueServiceImpl implements RevenueService {

    private final Logger log = LoggerFactory.getLogger(RevenueServiceImpl.class);

    private final RevenueRepository revenueRepository;

    private final RevenueSearchRepository revenueSearchRepository;

    public RevenueServiceImpl(RevenueRepository revenueRepository, RevenueSearchRepository revenueSearchRepository) {
        this.revenueRepository = revenueRepository;
        this.revenueSearchRepository = revenueSearchRepository;
    }

    /**
     * Save a revenue.
     *
     * @param revenue the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Revenue save(Revenue revenue) {
        log.debug("Request to save Revenue : {}", revenue);
        Revenue result = revenueRepository.save(revenue);
        revenueSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the revenues.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Revenue> findAll() {
        log.debug("Request to get all Revenues");
        return revenueRepository.findAll();
    }


    /**
     * Get one revenue by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Revenue> findOne(Long id) {
        log.debug("Request to get Revenue : {}", id);
        return revenueRepository.findById(id);
    }

    /**
     * Delete the revenue by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Revenue : {}", id);
        revenueRepository.deleteById(id);
        revenueSearchRepository.deleteById(id);
    }

    /**
     * Search for the revenue corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Revenue> search(String query) {
        log.debug("Request to search Revenues for query {}", query);
        return StreamSupport
            .stream(revenueSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
