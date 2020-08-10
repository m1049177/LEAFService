package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.MaintenanceService;
import com.mindtree.leafservice3.domain.Maintenance;
import com.mindtree.leafservice3.repository.MaintenanceRepository;
import com.mindtree.leafservice3.repository.search.MaintenanceSearchRepository;
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
 * Service Implementation for managing {@link Maintenance}.
 */
@Service
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {

    private final Logger log = LoggerFactory.getLogger(MaintenanceServiceImpl.class);

    private final MaintenanceRepository maintenanceRepository;

    private final MaintenanceSearchRepository maintenanceSearchRepository;

    public MaintenanceServiceImpl(MaintenanceRepository maintenanceRepository, MaintenanceSearchRepository maintenanceSearchRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.maintenanceSearchRepository = maintenanceSearchRepository;
    }

    /**
     * Save a maintenance.
     *
     * @param maintenance the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Maintenance save(Maintenance maintenance) {
        log.debug("Request to save Maintenance : {}", maintenance);
        Maintenance result = maintenanceRepository.save(maintenance);
        maintenanceSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the maintenances.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Maintenance> findAll() {
        log.debug("Request to get all Maintenances");
        return maintenanceRepository.findAll();
    }


    /**
     * Get one maintenance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Maintenance> findOne(Long id) {
        log.debug("Request to get Maintenance : {}", id);
        return maintenanceRepository.findById(id);
    }

    /**
     * Delete the maintenance by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Maintenance : {}", id);
        maintenanceRepository.deleteById(id);
        maintenanceSearchRepository.deleteById(id);
    }

    /**
     * Search for the maintenance corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Maintenance> search(String query) {
        log.debug("Request to search Maintenances for query {}", query);
        return StreamSupport
            .stream(maintenanceSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
