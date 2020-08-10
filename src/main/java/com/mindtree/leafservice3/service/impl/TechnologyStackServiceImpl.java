package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.TechnologyStackService;
import com.mindtree.leafservice3.domain.TechnologyStack;
import com.mindtree.leafservice3.repository.TechnologyStackRepository;
import com.mindtree.leafservice3.repository.search.TechnologyStackSearchRepository;
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
 * Service Implementation for managing {@link TechnologyStack}.
 */
@Service
@Transactional
public class TechnologyStackServiceImpl implements TechnologyStackService {

    private final Logger log = LoggerFactory.getLogger(TechnologyStackServiceImpl.class);

    private final TechnologyStackRepository technologyStackRepository;

    private final TechnologyStackSearchRepository technologyStackSearchRepository;

    public TechnologyStackServiceImpl(TechnologyStackRepository technologyStackRepository, TechnologyStackSearchRepository technologyStackSearchRepository) {
        this.technologyStackRepository = technologyStackRepository;
        this.technologyStackSearchRepository = technologyStackSearchRepository;
    }

    /**
     * Save a technologyStack.
     *
     * @param technologyStack the entity to save.
     * @return the persisted entity.
     */
    @Override
    public TechnologyStack save(TechnologyStack technologyStack) {
        log.debug("Request to save TechnologyStack : {}", technologyStack);
        TechnologyStack result = technologyStackRepository.save(technologyStack);
        technologyStackSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the technologyStacks.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TechnologyStack> findAll() {
        log.debug("Request to get all TechnologyStacks");
        return technologyStackRepository.findAll();
    }


    /**
     * Get one technologyStack by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TechnologyStack> findOne(Long id) {
        log.debug("Request to get TechnologyStack : {}", id);
        return technologyStackRepository.findById(id);
    }

    /**
     * Delete the technologyStack by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete TechnologyStack : {}", id);
        technologyStackRepository.deleteById(id);
        technologyStackSearchRepository.deleteById(id);
    }

    /**
     * Search for the technologyStack corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TechnologyStack> search(String query) {
        log.debug("Request to search TechnologyStacks for query {}", query);
        return StreamSupport
            .stream(technologyStackSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
