package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.CompanyService;
import com.mindtree.leafservice3.domain.Company;
import com.mindtree.leafservice3.repository.CompanyRepository;
import com.mindtree.leafservice3.repository.search.CompanySearchRepository;
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
 * Service Implementation for managing {@link Company}.
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private final CompanyRepository companyRepository;

    private final CompanySearchRepository companySearchRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanySearchRepository companySearchRepository) {
        this.companyRepository = companyRepository;
        this.companySearchRepository = companySearchRepository;
    }

    /**
     * Save a company.
     *
     * @param company the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Company save(Company company) {
        log.debug("Request to save Company : {}", company);
        Company result = companyRepository.save(company);
        companySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the companies.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Company> findAll() {
        log.debug("Request to get all Companies");
        return companyRepository.findAll();
    }


    /**
     * Get one company by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Company> findOne(Long id) {
        log.debug("Request to get Company : {}", id);
        return companyRepository.findById(id);
    }

    /**
     * Delete the company by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Company : {}", id);
        companyRepository.deleteById(id);
        companySearchRepository.deleteById(id);
    }

    /**
     * Search for the company corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Company> search(String query) {
        log.debug("Request to search Companies for query {}", query);
        return StreamSupport
            .stream(companySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
