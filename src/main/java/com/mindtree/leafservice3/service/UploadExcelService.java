package com.mindtree.leafservice3.service;

import com.mindtree.leafservice3.domain.Activity;
import com.mindtree.leafservice3.domain.Application;
import com.mindtree.leafservice3.domain.BusinessFunction;
import com.mindtree.leafservice3.domain.BusinessProcess;
import com.mindtree.leafservice3.domain.Capabilities;
import com.mindtree.leafservice3.domain.Employee;
import com.mindtree.leafservice3.domain.Expenditure;
import com.mindtree.leafservice3.domain.LineOfBusiness;
import com.mindtree.leafservice3.domain.OraganizationalUnit;
import com.mindtree.leafservice3.domain.Technology;
import com.mindtree.leafservice3.domain.TechnologyStack;
import com.mindtree.leafservice3.domain.UploadExcel;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link UploadExcel}.
 */
public interface UploadExcelService {

    /**
     * Save a uploadExcel.
     *
     * @param uploadExcel the entity to save.
     * @return the persisted entity.
     */
    UploadExcel save(UploadExcel uploadExcel);

    /**
     * Get all the uploadExcels.
     *
     * @return the list of entities.
     */
    List<UploadExcel> findAll();

    /**
     * Get the "id" uploadExcel.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UploadExcel> findOne(Long id);

    /**
     * Delete the "id" uploadExcel.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the uploadExcel corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<UploadExcel> search(String query);

    String functionDataUpload(Long company_id, MultipartFile readExcelDataFile);

    String appPortfolioUpload(Long company_id, MultipartFile readExcelDataFile);

    OraganizationalUnit checkForOrganizationalUnit(String orgName, String employee_id,String employee_name, Long company_id);

    LineOfBusiness checkForLOB(OraganizationalUnit orgData, String LobName, String employee_id, String employee_name);

    BusinessFunction checkForBF(LineOfBusiness lobData, String bfName, String bfType, String employee_id, String employee_name);

    Capabilities checkForCap(BusinessFunction bfData, String capName);

    BusinessProcess checkForBP(Capabilities capData, String bpName, Date startDate, Date EndDate, String bpStatus);

    Activity checkForActivity(BusinessProcess bpData, String name, String resourceRequired);

    Employee checkForEmployee(String employee_id, String employee_name);

    Application checkForApplication(LineOfBusiness lobData, String app_name, String app_type, String app_status, Date imp_date);

    TechnologyStack checkForTechnologyStack(String techName,String version, String techType);

    Technology checkForTechnology(Application appData, TechnologyStack technologyStackData);

    void checkForSpend(String type,Long spend_id, Integer amount, String successor);

    Expenditure checkForExpenditure(Application appData,String exp_type, String exp_desc, Date startDate, Date end_date);

    void checkForBudget(Application appData, Integer amount, String successor, Integer year);

}
