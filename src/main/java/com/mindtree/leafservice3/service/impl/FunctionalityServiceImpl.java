package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.FunctionalityService;
import com.mindtree.leafservice3.domain.Functionality;
import com.mindtree.leafservice3.repository.FunctionalityRepository;
import com.mindtree.leafservice3.repository.search.FunctionalitySearchRepository;
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
 * Service Implementation for managing {@link Functionality}.
 */
@Service
@Transactional
public class FunctionalityServiceImpl implements FunctionalityService {

    private final Logger log = LoggerFactory.getLogger(FunctionalityServiceImpl.class);

    private final FunctionalityRepository functionalityRepository;

    private final FunctionalitySearchRepository functionalitySearchRepository;

    public FunctionalityServiceImpl(FunctionalityRepository functionalityRepository, FunctionalitySearchRepository functionalitySearchRepository) {
        this.functionalityRepository = functionalityRepository;
        this.functionalitySearchRepository = functionalitySearchRepository;
    }

    /**
     * Save a functionality.
     *
     * @param functionality the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Functionality save(Functionality functionality) {
        log.debug("Request to save Functionality : {}", functionality);
        Functionality result = functionalityRepository.save(functionality);
        functionalitySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the functionalities.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Functionality> findAll() {
        log.debug("Request to get all Functionalities");
        return functionalityRepository.findAll();
    }


    /**
     * Get one functionality by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Functionality> findOne(Long id) {
        log.debug("Request to get Functionality : {}", id);
        return functionalityRepository.findById(id);
    }

    /**
     * Delete the functionality by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Functionality : {}", id);
        functionalityRepository.deleteById(id);
        functionalitySearchRepository.deleteById(id);
    }

    /**
     * Search for the functionality corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Functionality> search(String query) {
        log.debug("Request to search Functionalities for query {}", query);
        return StreamSupport
            .stream(functionalitySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
