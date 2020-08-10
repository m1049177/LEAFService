package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.OraganizationalUnitService;
import com.mindtree.leafservice3.domain.OraganizationalUnit;
import com.mindtree.leafservice3.repository.OraganizationalUnitRepository;
import com.mindtree.leafservice3.repository.search.OraganizationalUnitSearchRepository;
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
 * Service Implementation for managing {@link OraganizationalUnit}.
 */
@Service
@Transactional
public class OraganizationalUnitServiceImpl implements OraganizationalUnitService {

    private final Logger log = LoggerFactory.getLogger(OraganizationalUnitServiceImpl.class);

    private final OraganizationalUnitRepository oraganizationalUnitRepository;

    private final OraganizationalUnitSearchRepository oraganizationalUnitSearchRepository;

    public OraganizationalUnitServiceImpl(OraganizationalUnitRepository oraganizationalUnitRepository, OraganizationalUnitSearchRepository oraganizationalUnitSearchRepository) {
        this.oraganizationalUnitRepository = oraganizationalUnitRepository;
        this.oraganizationalUnitSearchRepository = oraganizationalUnitSearchRepository;
    }

    /**
     * Save a oraganizationalUnit.
     *
     * @param oraganizationalUnit the entity to save.
     * @return the persisted entity.
     */
    @Override
    public OraganizationalUnit save(OraganizationalUnit oraganizationalUnit) {
        log.debug("Request to save OraganizationalUnit : {}", oraganizationalUnit);
        OraganizationalUnit result = oraganizationalUnitRepository.save(oraganizationalUnit);
        oraganizationalUnitSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the oraganizationalUnits.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<OraganizationalUnit> findAll() {
        log.debug("Request to get all OraganizationalUnits");
        return oraganizationalUnitRepository.findAll();
    }


    /**
     * Get one oraganizationalUnit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<OraganizationalUnit> findOne(Long id) {
        log.debug("Request to get OraganizationalUnit : {}", id);
        return oraganizationalUnitRepository.findById(id);
    }

    /**
     * Delete the oraganizationalUnit by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete OraganizationalUnit : {}", id);
        oraganizationalUnitRepository.deleteById(id);
        oraganizationalUnitSearchRepository.deleteById(id);
    }

    /**
     * Search for the oraganizationalUnit corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<OraganizationalUnit> search(String query) {
        log.debug("Request to search OraganizationalUnits for query {}", query);
        return StreamSupport
            .stream(oraganizationalUnitSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
