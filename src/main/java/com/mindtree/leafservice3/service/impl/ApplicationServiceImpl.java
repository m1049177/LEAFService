package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.ApplicationService;
import com.mindtree.leafservice3.service.dto.ChartData;
import com.mindtree.leafservice3.domain.Application;
import com.mindtree.leafservice3.domain.LineOfBusiness;
import com.mindtree.leafservice3.domain.OraganizationalUnit;
import com.mindtree.leafservice3.domain.Technology;
import com.mindtree.leafservice3.repository.ApplicationRepository;
import com.mindtree.leafservice3.repository.LineOfBusinessRepository;
import com.mindtree.leafservice3.repository.OraganizationalUnitRepository;
import com.mindtree.leafservice3.repository.TechnologyRepository;
import com.mindtree.leafservice3.repository.search.ApplicationSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Application}.
 */
@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final Logger log = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private final ApplicationRepository applicationRepository;
    private final OraganizationalUnitRepository oraganizationalUnitRepository;
    private final LineOfBusinessRepository lineOfBusinessRepository;
    private final TechnologyRepository technologyRepository;

    private final ApplicationSearchRepository applicationSearchRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository, 
            OraganizationalUnitRepository oraganizationalUnitRepository,
            LineOfBusinessRepository lineOfBusinessRepository, 
            ApplicationSearchRepository applicationSearchRepository,
            TechnologyRepository technologyRepository) {
        this.applicationRepository = applicationRepository;
        this.applicationSearchRepository = applicationSearchRepository;
        this.oraganizationalUnitRepository = oraganizationalUnitRepository;
        this.lineOfBusinessRepository = lineOfBusinessRepository;
        this.technologyRepository = technologyRepository;
    }

    /**
     * Save a application.
     *
     * @param application the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Application save(Application application) {
        log.debug("Request to save Application : {}", application);
        Application result = applicationRepository.save(application);
        applicationSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the applications.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Application> findAll() {
        log.debug("Request to get all Applications");
        return applicationRepository.findAll();
    }


    /**
     * Get one application by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Application> findOne(Long id) {
        log.debug("Request to get Application : {}", id);
        return applicationRepository.findById(id);
    }

    /**
     * Delete the application by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Application : {}", id);
        applicationRepository.deleteById(id);
        applicationSearchRepository.deleteById(id);
    }

    /**
     * Search for the application corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Application> search(String query) {
        log.debug("Request to search Applications for query {}", query);
        return StreamSupport
            .stream(applicationSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
    @Override
    public List<ChartData> organizationalChartData() {
        log.debug("REST request to get organizational chart data");
        List<OraganizationalUnit> organizationalUnit = oraganizationalUnitRepository.findAll();
        List<LineOfBusiness> lineOfBusiness = lineOfBusinessRepository.findAll();
        List<Application> applications = applicationRepository.findAll();
        List<ChartData> parent = new ArrayList<>();

        for (OraganizationalUnit organization : organizationalUnit) {
            List<ChartData> children = new ArrayList<>();
            for (LineOfBusiness LOB : lineOfBusiness) {
                if (organization.getId() == LOB.getOraganizationalUnit().getId()) {
                    List<ChartData> grandChildren = new ArrayList<>();
                    for(Application application : applications) {
                        if(application.getLineOfBusiness().getId() == LOB.getId()) {
                            ChartData childrenObj = new ChartData();
                                childrenObj.id = application.getId();
                                childrenObj.name = application.getName();
                                childrenObj.title = "applications";
                                grandChildren.add(childrenObj);
                            }
                        }
                        ChartData LOBObj = new ChartData();
                        LOBObj.id = LOB.getId();
                        LOBObj.name = LOB.getName();
                        LOBObj.title = "line-of-businesses";
                        
                        children.add(LOBObj);
                    }
                    
                }
                ChartData OrgObj = new ChartData();
                OrgObj.id = organization.getId();
                OrgObj.name = organization.getName();
                OrgObj.title = "oraganizational-units";
            
                parent.add(OrgObj);
            }
        return parent;
    }
    
    @Override
    public List<ChartData> getAppChartData(Long company_id) {
        List<ChartData> chartData = new ArrayList<>();
        List<LineOfBusiness> lineOfBusinesses = lineOfBusinessRepository.findAll();
        List<Application> applications = applicationRepository.findAll();
        
        for(LineOfBusiness lob : lineOfBusinesses) {
            if(lob.getOraganizationalUnit().getCompany().getId() == company_id) {
                ChartData lob_data = new ChartData();
                    lob_data.id = lob.getId();
                    lob_data.name = lob.getName();
                    lob_data.title = "line-of-businesses";
                    lob_data.trackingId = lob.getName() +"-Lob"+ lob.getId();

                chartData.add(lob_data);
            }
        }

        for(Application app : applications) {
            if(app.getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == company_id) {
                ChartData appData = new ChartData();
                    appData.id = app.getId();
                    appData.name = app.getName();
                    appData.title = "applications";
                    appData.trackingId = app.getName() +"-App"+ app.getId();
                    appData.parentId = app.getLineOfBusiness().getName() +"-Lob"+ app.getLineOfBusiness().getId();
                    appData.parentName = app.getLineOfBusiness().getName();

                chartData.add(appData);
            }
        }
        // for(Technology tech : technologies) {
        //     if(tech.getApplication().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == company_id) {
        //         ChartData techData = new ChartData();
        //             techData.id = tech.getId();
        //             techData.name = tech.getTechnologyStack().getName();
        //             techData.title = "Technology";
        //             techData.trackingId = tech.getTechnologyStack().getName() +"-Tech"+ tech.getId();
        //             techData.parentId = tech.getApplication().getName() +"-App"+ tech.getApplication().getId();
        //             techData.parentName = tech.getApplication().getName();
    
        //             chartData.add(techData);
        //         }
        // }
        return chartData;
    }
}
