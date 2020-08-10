package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.TechnologyService;

import com.mindtree.leafservice3.repository.TechnologyStackRepository;
import com.mindtree.leafservice3.service.dto.TechnologyAppCount;
import com.mindtree.leafservice3.domain.TechnologyStack;
import com.mindtree.leafservice3.domain.Technology;
import com.mindtree.leafservice3.repository.LabelRepository;
import com.mindtree.leafservice3.repository.TechnologyRepository;
import com.mindtree.leafservice3.repository.search.TechnologySearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Technology}.
 */
@Service
@Transactional
public class TechnologyServiceImpl implements TechnologyService {

    private final Logger log = LoggerFactory.getLogger(TechnologyServiceImpl.class);

    private final TechnologyRepository technologyRepository;
    private final TechnologyStackRepository technologyStackRepository;
    private final LabelRepository labelRepository;

    private final TechnologySearchRepository technologySearchRepository;

    public TechnologyServiceImpl(TechnologyRepository technologyRepository, TechnologySearchRepository technologySearchRepository,
    		TechnologyStackRepository technologyStackRepository, LabelRepository labelRepository) {
        this.technologyRepository = technologyRepository;
        this.technologySearchRepository = technologySearchRepository;
        this.technologyStackRepository = technologyStackRepository;
        this.labelRepository = labelRepository;
        
    }

    /**
     * Save a technology.
     *
     * @param technology the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Technology save(Technology technology) {
        log.debug("Request to save Technology : {}", technology);
        Technology result = technologyRepository.save(technology);
        technologySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the technologies.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Technology> findAll() {
        log.debug("Request to get all Technologies");
        return technologyRepository.findAll();
    }


    /**
     * Get one technology by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Technology> findOne(Long id) {
        log.debug("Request to get Technology : {}", id);
        return technologyRepository.findById(id);
    }

    /**
     * Delete the technology by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Technology : {}", id);
        technologyRepository.deleteById(id);
        technologySearchRepository.deleteById(id);
    }

    /**
     * Search for the technology corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Technology> search(String query) {
        log.debug("Request to search Technologies for query {}", query);
        return StreamSupport
            .stream(technologySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<TechnologyAppCount> technologyCount(Long company_id) {
       List<Technology> technology = technologyRepository.findAll();
       List<TechnologyStack> technologyStack = technologyStackRepository.findAll();
       List<TechnologyAppCount> technologyData = new ArrayList<>();
       for(TechnologyStack techStack : technologyStack) {
    	   TechnologyAppCount technologyAppCount = new TechnologyAppCount();
    	   int count = 0;
    	   for(Technology tech : technology) {
    		   if(tech.getTechnologyStack().getId() == techStack.getId() && tech.getApplication().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == company_id) {
    			  count = count + 1;
    		   }
    	   }
    	   if(count > 0) {
    		   technologyAppCount.count = count;
    		   technologyAppCount.name = techStack.getName();
    		   technologyAppCount.type = techStack.getType().toString();
    		   technologyData.add(technologyAppCount);
    	   }
    	   count = 0;
       }
       return technologyData;
    }
    
}
