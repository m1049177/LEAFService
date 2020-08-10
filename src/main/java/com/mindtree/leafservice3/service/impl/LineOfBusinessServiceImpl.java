package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.LineOfBusinessService;
import com.mindtree.leafservice3.service.dto.LobSearchData;
import com.mindtree.leafservice3.domain.Activity;
import com.mindtree.leafservice3.domain.BusinessFunction;
import com.mindtree.leafservice3.domain.BusinessProcess;
import com.mindtree.leafservice3.domain.Capabilities;
import com.mindtree.leafservice3.domain.LineOfBusiness;
import com.mindtree.leafservice3.domain.Task;
import com.mindtree.leafservice3.repository.ActivityRepository;
import com.mindtree.leafservice3.repository.BusinessFunctionRepository;
import com.mindtree.leafservice3.repository.BusinessProcessRepository;
import com.mindtree.leafservice3.repository.CapabilitiesRepository;
import com.mindtree.leafservice3.repository.LineOfBusinessRepository;
import com.mindtree.leafservice3.repository.TaskRepository;
import com.mindtree.leafservice3.repository.search.LineOfBusinessSearchRepository;
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
 * Service Implementation for managing {@link LineOfBusiness}.
 */
@Service
@Transactional
public class LineOfBusinessServiceImpl implements LineOfBusinessService {

    private final Logger log = LoggerFactory.getLogger(LineOfBusinessServiceImpl.class);

    private final LineOfBusinessRepository lineOfBusinessRepository;

    private final BusinessFunctionRepository businessFunctionRepository;

    private final CapabilitiesRepository capabilitiesRepository;

    private final BusinessProcessRepository businessProcessRepository;

    private final ActivityRepository activityRepository;

    private final TaskRepository taskRepository;

    private final LineOfBusinessSearchRepository lineOfBusinessSearchRepository;

    public LineOfBusinessServiceImpl(LineOfBusinessRepository lineOfBusinessRepository, 
    LineOfBusinessSearchRepository lineOfBusinessSearchRepository,
    BusinessFunctionRepository businessFunctionRepository, 
    CapabilitiesRepository capabilitiesRepository, 
    BusinessProcessRepository businessProcessRepository, 
    ActivityRepository activityRepository,
    TaskRepository taskRepository ) {
        this.lineOfBusinessRepository = lineOfBusinessRepository;
        this.lineOfBusinessSearchRepository = lineOfBusinessSearchRepository;
        this.businessFunctionRepository = businessFunctionRepository;
        this.businessProcessRepository = businessProcessRepository;
        this.capabilitiesRepository = capabilitiesRepository;
        this.activityRepository = activityRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Save a lineOfBusiness.
     *
     * @param lineOfBusiness the entity to save.
     * @return the persisted entity.
     */
    @Override
    public LineOfBusiness save(LineOfBusiness lineOfBusiness) {
        log.debug("Request to save LineOfBusiness : {}", lineOfBusiness);
        LineOfBusiness result = lineOfBusinessRepository.save(lineOfBusiness);
        lineOfBusinessSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the lineOfBusinesses.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LineOfBusiness> findAll() {
        log.debug("Request to get all LineOfBusinesses");
        return lineOfBusinessRepository.findAll();
    }


    /**
     * Get one lineOfBusiness by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LineOfBusiness> findOne(Long id) {
        log.debug("Request to get LineOfBusiness : {}", id);
        return lineOfBusinessRepository.findById(id);
    }

    /**
     * Delete the lineOfBusiness by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete LineOfBusiness : {}", id);
        lineOfBusinessRepository.deleteById(id);
        lineOfBusinessSearchRepository.deleteById(id);
    }

    @Override
    public LobSearchData searchData(Long lob_id) {
        log.debug("Request to search data for lob id : {}", lob_id);
        LobSearchData lob_data = new LobSearchData();
        Optional<LineOfBusiness> lob = lineOfBusinessRepository.findById(lob_id);
        lob_data.id = lob.get().getId();
        lob_data.name = lob.get().getName();

        lob_data.bFunction = businessFunctionRepository.findAll();
        // lob_data.bFunction = bfunction.stream()
        //                     .filter(value -> value.getLineOfBusiness().getId().equals(lob_id))
        //                     .collect(Collectors.toList());
        
        lob_data.capability = capabilitiesRepository.findAll();
        // lob_data.capability = capabilities.stream()
        //                     .filter(value -> value.getBusinessFunction().getLineOfBusiness().getId().equals(lob_id))
        //                     .collect(Collectors.toList());
        
        lob_data.bProcesses  = businessProcessRepository.findAll();
        // lob_data.bProcesses = businessProcess.stream()
        //                     .filter(value -> value.getCapabilities().getBusinessFunction().getLineOfBusiness().getId().equals(lob_id))
        //                     .collect(Collectors.toList());

         lob_data.activities = activityRepository.findAll();
        // lob_data.activities = activity.stream()
        //                     .filter(value -> value.getBusinessProcess().getCapabilities().getBusinessFunction().getLineOfBusiness().getId().equals(lob_id))
        //                     .collect(Collectors.toList());

        lob_data.tasks = taskRepository.findAll();
        // lob_data.tasks = taskData.stream()
        //                 .filter(value -> value.getActivity().getBusinessProcess().getCapabilities().getBusinessFunction().getLineOfBusiness().getId().equals(lob_id))
        //                 .collect(Collectors.toList());

        return lob_data;
 
    }
    /**
     * Search for the lineOfBusiness corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<LineOfBusiness> search(String query) {
        log.debug("Request to search LineOfBusinesses for query {}", query);
        return StreamSupport
            .stream(lineOfBusinessSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
