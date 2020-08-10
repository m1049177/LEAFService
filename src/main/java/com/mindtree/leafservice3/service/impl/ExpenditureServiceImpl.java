package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.ExpenditureService;
import com.mindtree.leafservice3.domain.Expenditure;
import com.mindtree.leafservice3.repository.ExpenditureRepository;
import com.mindtree.leafservice3.repository.search.ExpenditureSearchRepository;
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
 * Service Implementation for managing {@link Expenditure}.
 */
@Service
@Transactional
public class ExpenditureServiceImpl implements ExpenditureService {

    private final Logger log = LoggerFactory.getLogger(ExpenditureServiceImpl.class);

    private final ExpenditureRepository expenditureRepository;

    private final ExpenditureSearchRepository expenditureSearchRepository;

    public ExpenditureServiceImpl(ExpenditureRepository expenditureRepository, ExpenditureSearchRepository expenditureSearchRepository) {
        this.expenditureRepository = expenditureRepository;
        this.expenditureSearchRepository = expenditureSearchRepository;
    }

    /**
     * Save a expenditure.
     *
     * @param expenditure the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Expenditure save(Expenditure expenditure) {
        log.debug("Request to save Expenditure : {}", expenditure);
        Expenditure result = expenditureRepository.save(expenditure);
        expenditureSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the expenditures.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Expenditure> findAll() {
        log.debug("Request to get all Expenditures");
        return expenditureRepository.findAll();
    }


    /**
     * Get one expenditure by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Expenditure> findOne(Long id) {
        log.debug("Request to get Expenditure : {}", id);
        return expenditureRepository.findById(id);
    }

    /**
     * Delete the expenditure by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Expenditure : {}", id);
        expenditureRepository.deleteById(id);
        expenditureSearchRepository.deleteById(id);
    }

    /**
     * Search for the expenditure corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Expenditure> search(String query) {
        log.debug("Request to search Expenditures for query {}", query);
        return StreamSupport
            .stream(expenditureSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
