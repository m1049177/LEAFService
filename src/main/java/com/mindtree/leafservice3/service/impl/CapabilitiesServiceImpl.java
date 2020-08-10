package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.CapabilitiesService;
import com.mindtree.leafservice3.service.dto.ChartData;
import com.mindtree.leafservice3.domain.BusinessFunction;
import com.mindtree.leafservice3.domain.Capabilities;
import com.mindtree.leafservice3.domain.Company;
import com.mindtree.leafservice3.domain.LineOfBusiness;
import com.mindtree.leafservice3.domain.OraganizationalUnit;
import com.mindtree.leafservice3.repository.BusinessFunctionRepository;
import com.mindtree.leafservice3.repository.CapabilitiesRepository;
import com.mindtree.leafservice3.repository.CompanyRepository;
import com.mindtree.leafservice3.repository.LineOfBusinessRepository;
import com.mindtree.leafservice3.repository.OraganizationalUnitRepository;
import com.mindtree.leafservice3.repository.search.CapabilitiesSearchRepository;
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
 * Service Implementation for managing {@link Capabilities}.
 */
@Service
@Transactional
public class CapabilitiesServiceImpl implements CapabilitiesService {

    private final Logger log = LoggerFactory.getLogger(CapabilitiesServiceImpl.class);

    private final CapabilitiesRepository capabilitiesRepository;

    private final CapabilitiesSearchRepository capabilitiesSearchRepository;
    private final OraganizationalUnitRepository oraganizationalUnitRepository;
    private final LineOfBusinessRepository lineOfBusinessRepository;
    private final BusinessFunctionRepository businessFunctionRepository;
    private final CompanyRepository companyRepository;

    public CapabilitiesServiceImpl(CapabilitiesRepository capabilitiesRepository,
            CapabilitiesSearchRepository capabilitiesSearchRepository,
            OraganizationalUnitRepository oraganizationalUnitRepository,
            LineOfBusinessRepository lineOfBusinessRepository, 
            BusinessFunctionRepository businessFunctionRepository,
            CompanyRepository companyRepository) {
        this.capabilitiesRepository = capabilitiesRepository;
        this.capabilitiesSearchRepository = capabilitiesSearchRepository;
        this.oraganizationalUnitRepository = oraganizationalUnitRepository;
        this.lineOfBusinessRepository = lineOfBusinessRepository;
        this.businessFunctionRepository = businessFunctionRepository;
        this.companyRepository = companyRepository;
    }

    /**
     * Save a capabilities.
     *
     * @param capabilities the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Capabilities save(Capabilities capabilities) {
        log.debug("Request to save Capabilities : {}", capabilities);
        Capabilities result = capabilitiesRepository.save(capabilities);
        capabilitiesSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the capabilities.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Capabilities> findAll() {
        log.debug("Request to get all Capabilities");
        return capabilitiesRepository.findAll();
    }

    /**
     * Get one capabilities by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Capabilities> findOne(Long id) {
        log.debug("Request to get Capabilities : {}", id);
        return capabilitiesRepository.findById(id);
    }

    /**
     * Delete the capabilities by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Capabilities : {}", id);
        capabilitiesRepository.deleteById(id);
        capabilitiesSearchRepository.deleteById(id);
    }

    /**
     * Search for the capabilities corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Capabilities> search(String query) {
        log.debug("Request to search Capabilities for query {}", query);
        return StreamSupport.stream(capabilitiesSearchRepository.search(queryStringQuery(query)).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChartData> organizationalChartData(Long company_id) {
        log.debug("REST request to get organizational chart data");
        List<OraganizationalUnit> organizationalUnit = oraganizationalUnitRepository.findAll();
        List<LineOfBusiness> lineOfBusiness = lineOfBusinessRepository.findAll();
        List<BusinessFunction> businessFunction = businessFunctionRepository.findAll();
        List<Capabilities> capabilities = capabilitiesRepository.findAll();
        List<ChartData> orgData = new ArrayList<>();
        Optional<Company> company = companyRepository.findById(company_id);
        ChartData data = new ChartData();
            data.id = company.get().getId();
            data.name = company.get().getCompanyName();
            data.title = "Company";
            data.trackingId = company.get().getCompanyName()+"-Comp"+company.get().getId();
            orgData.add(data);

        for (OraganizationalUnit organization : organizationalUnit) {
            if(organization.getCompany().getId() == company_id) {
                ChartData orgUnit = new ChartData();
                orgUnit.id = organization.getId();
                orgUnit.name = organization.getName();
                orgUnit.parentId = company.get().getCompanyName()+"-Comp"+company.get().getId();
                orgUnit.parentName = company.get().getCompanyName();
                orgUnit.title = "oraganizational-units";
                orgUnit.trackingId = organization.getName() +"-Org"+ organization.getId();
    
                orgData.add(orgUnit);
            }
          
        }
        for (LineOfBusiness LOB : lineOfBusiness) {
            if(LOB.getOraganizationalUnit().getCompany().getId() == company_id) {
                ChartData lobData = new ChartData();
                lobData.id = LOB.getId();
                lobData.name = LOB.getName();
                lobData.parentId = LOB.getOraganizationalUnit().getName()+"-Org"+ LOB.getOraganizationalUnit().getId();
                lobData.parentName = LOB.getOraganizationalUnit().getName();
                lobData.title = "line-of-businesses";
                lobData.trackingId = LOB.getName() +"-Lob"+ LOB.getId();
    
                orgData.add(lobData);
            }

        }
        for (BusinessFunction bFunction : businessFunction) {
            if(bFunction.getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == company_id) {
                ChartData bfData = new ChartData();
                bfData.id = bFunction.getId();
                bfData.name = bFunction.getName();
                bfData.parentId =bFunction.getLineOfBusiness().getName()+"-Lob" +bFunction.getLineOfBusiness().getId();
                bfData.parentName = bFunction.getLineOfBusiness().getName();
                bfData.title = "business-functions";
                bfData.trackingId = bFunction.getName()+"-bf"+ bFunction.getId();
    
                orgData.add(bfData);
            }

        }
        for (Capabilities capability : capabilities) {
            if(capability.getBusinessFunction().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == company_id) {
                ChartData capData = new ChartData();
                capData.id = capability.getId();
                capData.name = capability.getDescription();
                capData.parentId = capability.getBusinessFunction().getName() +"-bf"+ capability.getBusinessFunction().getId();
                capData.parentName = capability.getBusinessFunction().getName();
                capData.title = "capabilities";
                capData.trackingId = capability.getDescription() +"-Cap" + capability.getId();
    
                orgData.add(capData);
            }
        }
        return orgData;
    }

}
