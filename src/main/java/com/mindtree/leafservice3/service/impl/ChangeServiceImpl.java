package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.ChangeService;
import com.mindtree.leafservice3.domain.Change;
import com.mindtree.leafservice3.repository.ChangeRepository;
import com.mindtree.leafservice3.repository.search.ChangeSearchRepository;
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
 * Service Implementation for managing {@link Change}.
 */
@Service
@Transactional
public class ChangeServiceImpl implements ChangeService {

    private final Logger log = LoggerFactory.getLogger(ChangeServiceImpl.class);

    private final ChangeRepository changeRepository;

    private final ChangeSearchRepository changeSearchRepository;

    public ChangeServiceImpl(ChangeRepository changeRepository, ChangeSearchRepository changeSearchRepository) {
        this.changeRepository = changeRepository;
        this.changeSearchRepository = changeSearchRepository;
    }

    /**
     * Save a change.
     *
     * @param change the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Change save(Change change) {
        log.debug("Request to save Change : {}", change);
        Change result = changeRepository.save(change);
        changeSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the changes.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Change> findAll() {
        log.debug("Request to get all Changes");
        return changeRepository.findAll();
    }


    /**
     * Get one change by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Change> findOne(Long id) {
        log.debug("Request to get Change : {}", id);
        return changeRepository.findById(id);
    }

    /**
     * Delete the change by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Change : {}", id);
        changeRepository.deleteById(id);
        changeSearchRepository.deleteById(id);
    }

    /**
     * Search for the change corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Change> search(String query) {
        log.debug("Request to search Changes for query {}", query);
        return StreamSupport
            .stream(changeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
