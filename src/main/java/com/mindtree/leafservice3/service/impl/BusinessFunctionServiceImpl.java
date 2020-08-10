package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.BusinessFunctionService;
import com.mindtree.leafservice3.domain.BusinessFunction;
import com.mindtree.leafservice3.repository.BusinessFunctionRepository;
import com.mindtree.leafservice3.repository.search.BusinessFunctionSearchRepository;
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
 * Service Implementation for managing {@link BusinessFunction}.
 */
@Service
@Transactional
public class BusinessFunctionServiceImpl implements BusinessFunctionService {

    private final Logger log = LoggerFactory.getLogger(BusinessFunctionServiceImpl.class);

    private final BusinessFunctionRepository businessFunctionRepository;

    private final BusinessFunctionSearchRepository businessFunctionSearchRepository;

    public BusinessFunctionServiceImpl(BusinessFunctionRepository businessFunctionRepository, BusinessFunctionSearchRepository businessFunctionSearchRepository) {
        this.businessFunctionRepository = businessFunctionRepository;
        this.businessFunctionSearchRepository = businessFunctionSearchRepository;
    }

    /**
     * Save a businessFunction.
     *
     * @param businessFunction the entity to save.
     * @return the persisted entity.
     */
    @Override
    public BusinessFunction save(BusinessFunction businessFunction) {
        log.debug("Request to save BusinessFunction : {}", businessFunction);
        BusinessFunction result = businessFunctionRepository.save(businessFunction);
        businessFunctionSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the businessFunctions.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BusinessFunction> findAll() {
        log.debug("Request to get all BusinessFunctions");
        return businessFunctionRepository.findAll();
    }


    /**
     * Get one businessFunction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<BusinessFunction> findOne(Long id) {
        log.debug("Request to get BusinessFunction : {}", id);
        return businessFunctionRepository.findById(id);
    }

    /**
     * Delete the businessFunction by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete BusinessFunction : {}", id);
        businessFunctionRepository.deleteById(id);
        businessFunctionSearchRepository.deleteById(id);
    }

    /**
     * Search for the businessFunction corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BusinessFunction> search(String query) {
        log.debug("Request to search BusinessFunctions for query {}", query);
        return StreamSupport
            .stream(businessFunctionSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
