package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.BrandService;
import com.mindtree.leafservice3.domain.Brand;
import com.mindtree.leafservice3.repository.BrandRepository;
import com.mindtree.leafservice3.repository.search.BrandSearchRepository;
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
 * Service Implementation for managing {@link Brand}.
 */
@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    private final Logger log = LoggerFactory.getLogger(BrandServiceImpl.class);

    private final BrandRepository brandRepository;

    private final BrandSearchRepository brandSearchRepository;

    public BrandServiceImpl(BrandRepository brandRepository, BrandSearchRepository brandSearchRepository) {
        this.brandRepository = brandRepository;
        this.brandSearchRepository = brandSearchRepository;
    }

    /**
     * Save a brand.
     *
     * @param brand the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Brand save(Brand brand) {
        log.debug("Request to save Brand : {}", brand);
        Brand result = brandRepository.save(brand);
        brandSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the brands.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Brand> findAll() {
        log.debug("Request to get all Brands");
        return brandRepository.findAll();
    }


    /**
     * Get one brand by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Brand> findOne(Long id) {
        log.debug("Request to get Brand : {}", id);
        return brandRepository.findById(id);
    }

    /**
     * Delete the brand by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Brand : {}", id);
        brandRepository.deleteById(id);
        brandSearchRepository.deleteById(id);
    }

    /**
     * Search for the brand corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Brand> search(String query) {
        log.debug("Request to search Brands for query {}", query);
        return StreamSupport
            .stream(brandSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
