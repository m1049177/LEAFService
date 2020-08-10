package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.BusinessProcessService;
import com.mindtree.leafservice3.domain.BusinessProcess;
import com.mindtree.leafservice3.repository.BusinessProcessRepository;
import com.mindtree.leafservice3.repository.search.BusinessProcessSearchRepository;
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
 * Service Implementation for managing {@link BusinessProcess}.
 */
@Service
@Transactional
public class BusinessProcessServiceImpl implements BusinessProcessService {

    private final Logger log = LoggerFactory.getLogger(BusinessProcessServiceImpl.class);

    private final BusinessProcessRepository businessProcessRepository;

    private final BusinessProcessSearchRepository businessProcessSearchRepository;

    public BusinessProcessServiceImpl(BusinessProcessRepository businessProcessRepository, BusinessProcessSearchRepository businessProcessSearchRepository) {
        this.businessProcessRepository = businessProcessRepository;
        this.businessProcessSearchRepository = businessProcessSearchRepository;
    }

    /**
     * Save a businessProcess.
     *
     * @param businessProcess the entity to save.
     * @return the persisted entity.
     */
    @Override
    public BusinessProcess save(BusinessProcess businessProcess) {
        log.debug("Request to save BusinessProcess : {}", businessProcess);
        BusinessProcess result = businessProcessRepository.save(businessProcess);
        businessProcessSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the businessProcesses.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BusinessProcess> findAll() {
        log.debug("Request to get all BusinessProcesses");
        return businessProcessRepository.findAll();
    }


    /**
     * Get one businessProcess by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<BusinessProcess> findOne(Long id) {
        log.debug("Request to get BusinessProcess : {}", id);
        return businessProcessRepository.findById(id);
    }

    /**
     * Delete the businessProcess by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete BusinessProcess : {}", id);
        businessProcessRepository.deleteById(id);
        businessProcessSearchRepository.deleteById(id);
    }

    /**
     * Search for the businessProcess corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<BusinessProcess> search(String query) {
        log.debug("Request to search BusinessProcesses for query {}", query);
        return StreamSupport
            .stream(businessProcessSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
