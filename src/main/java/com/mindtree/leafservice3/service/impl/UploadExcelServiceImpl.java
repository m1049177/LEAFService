package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.UploadExcelService;
import com.mindtree.leafservice3.domain.Activity;
import com.mindtree.leafservice3.domain.Application;
import com.mindtree.leafservice3.domain.Budget;
import com.mindtree.leafservice3.domain.BusinessFunction;
import com.mindtree.leafservice3.domain.BusinessProcess;
import com.mindtree.leafservice3.domain.Capabilities;
import com.mindtree.leafservice3.domain.Company;
import com.mindtree.leafservice3.domain.Employee;
import com.mindtree.leafservice3.domain.Expenditure;
import com.mindtree.leafservice3.domain.LineOfBusiness;
import com.mindtree.leafservice3.domain.OraganizationalUnit;
import com.mindtree.leafservice3.domain.Spend;
import com.mindtree.leafservice3.domain.Technology;
import com.mindtree.leafservice3.domain.TechnologyStack;
import com.mindtree.leafservice3.domain.UploadExcel;
import com.mindtree.leafservice3.domain.enumeration.ApplicationStatus;
import com.mindtree.leafservice3.domain.enumeration.ApplicationType;
import com.mindtree.leafservice3.domain.enumeration.BusinessFunctionType;
import com.mindtree.leafservice3.domain.enumeration.CurrencySuccessor;
import com.mindtree.leafservice3.domain.enumeration.ProcessStatus;
import com.mindtree.leafservice3.domain.enumeration.ResourcesRequired;
import com.mindtree.leafservice3.repository.ActivityRepository;
import com.mindtree.leafservice3.repository.ApplicationRepository;
import com.mindtree.leafservice3.repository.BudgetRepository;
import com.mindtree.leafservice3.repository.BusinessFunctionRepository;
import com.mindtree.leafservice3.repository.BusinessProcessRepository;
import com.mindtree.leafservice3.repository.CapabilitiesRepository;
import com.mindtree.leafservice3.repository.CompanyRepository;
import com.mindtree.leafservice3.repository.EmployeeRepository;
import com.mindtree.leafservice3.repository.ExpenditureRepository;
import com.mindtree.leafservice3.repository.LineOfBusinessRepository;
import com.mindtree.leafservice3.repository.OraganizationalUnitRepository;
import com.mindtree.leafservice3.repository.SpendRepository;
import com.mindtree.leafservice3.repository.TechnologyRepository;
import com.mindtree.leafservice3.repository.TechnologyStackRepository;
import com.mindtree.leafservice3.repository.UploadExcelRepository;
import com.mindtree.leafservice3.repository.search.ActivitySearchRepository;
import com.mindtree.leafservice3.repository.search.ApplicationSearchRepository;
import com.mindtree.leafservice3.repository.search.TechnologySearchRepository;
import com.mindtree.leafservice3.repository.search.UploadExcelSearchRepository;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link UploadExcel}.
 */
@Service
@Transactional
public class UploadExcelServiceImpl implements UploadExcelService {

    private final Logger log = LoggerFactory.getLogger(UploadExcelServiceImpl.class);

    public ZoneId defaultZoneId = ZoneId.systemDefault();
    public Date toDay = new Date();

    private final UploadExcelRepository uploadExcelRepository;

    private final UploadExcelSearchRepository uploadExcelSearchRepository;
    private final EmployeeRepository employeeRepository;
    private final OraganizationalUnitRepository oraganizationalUnitRepository;
    private final LineOfBusinessRepository lineOfBusinessRepository;
    private final BusinessFunctionRepository businessFunctionRepository;
    private final CapabilitiesRepository capabilitiesRepository;
    private final BusinessProcessRepository businessProcessRepository;
    private final ActivityRepository activityRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;
    private final TechnologyStackRepository technologyStackRepository;
    private final TechnologyRepository technologyRepository;
    private final ExpenditureRepository expenditureRepository;
    private final SpendRepository spendRepository;
    private final BudgetRepository budgetRepository;
    private final ActivitySearchRepository activitySearchRepository;
    private final ApplicationSearchRepository applicationSearchRepository;
    private final TechnologySearchRepository technologySearchRepository;

    public UploadExcelServiceImpl(UploadExcelRepository uploadExcelRepository,
            EmployeeRepository employeeRepository,
            UploadExcelSearchRepository uploadExcelSearchRepository,
            CapabilitiesRepository capabilitiesRepository,
            OraganizationalUnitRepository oraganizationalUnitRepository,
            LineOfBusinessRepository lineOfBusinessRepository, 
            BusinessFunctionRepository businessFunctionRepository,
            BusinessProcessRepository businessProcessRepository,
            ActivityRepository activityRepository,
            CompanyRepository companyRepository,
            ApplicationRepository applicationRepository,
            TechnologyStackRepository technologyStackRepository, 
            TechnologyRepository technologyRepository,
            ExpenditureRepository expenditureRepository, 
            SpendRepository spendRepository,
            BudgetRepository budgetRepository,
            ActivitySearchRepository activitySearchRepository,
            ApplicationSearchRepository applicationSearchRepository,
            TechnologySearchRepository technologySearchRepository
            ) {
        this.uploadExcelRepository = uploadExcelRepository;
        this.uploadExcelSearchRepository = uploadExcelSearchRepository;
        this.oraganizationalUnitRepository = oraganizationalUnitRepository;
        this.lineOfBusinessRepository = lineOfBusinessRepository;
        this.businessFunctionRepository = businessFunctionRepository;
        this.capabilitiesRepository = capabilitiesRepository;
        this.businessProcessRepository = businessProcessRepository;
        this.activityRepository = activityRepository;
        this.employeeRepository = employeeRepository;
        this.companyRepository = companyRepository;
        this.applicationRepository = applicationRepository;
        this.technologyStackRepository = technologyStackRepository;
        this.technologyRepository = technologyRepository;
        this.expenditureRepository = expenditureRepository;
        this.spendRepository = spendRepository;
        this.budgetRepository = budgetRepository;
        this.activitySearchRepository = activitySearchRepository;
        this.applicationSearchRepository = applicationSearchRepository;
        this.technologySearchRepository = technologySearchRepository;

    }

    /**
     * Save a uploadExcel.
     *
     * @param uploadExcel the entity to save.
     * @return the persisted entity.
     */
    @Override
    public UploadExcel save(UploadExcel uploadExcel) {
        log.debug("Request to save UploadExcel : {}", uploadExcel);
        UploadExcel result = uploadExcelRepository.save(uploadExcel);
        uploadExcelSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the uploadExcels.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UploadExcel> findAll() {
        log.debug("Request to get all UploadExcels");
        return uploadExcelRepository.findAll();
    }


    /**
     * Get one uploadExcel by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UploadExcel> findOne(Long id) {
        log.debug("Request to get UploadExcel : {}", id);
        return uploadExcelRepository.findById(id);
    }

    /**
     * Delete the uploadExcel by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UploadExcel : {}", id);
        uploadExcelRepository.deleteById(id);
        uploadExcelSearchRepository.deleteById(id);
    }

    /**
     * Search for the uploadExcel corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UploadExcel> search(String query) {
        log.debug("Request to search UploadExcels for query {}", query);
        return StreamSupport
            .stream(uploadExcelSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

    @Override
    public String functionDataUpload(Long company_id,MultipartFile readExcelDataFile) throws RuntimeException {
        String result = " ";
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(readExcelDataFile.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);

            for (int i = 2; i < worksheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = worksheet.getRow(i);
                OraganizationalUnit org_data = this.checkForOrganizationalUnit(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue(), row.getCell(2).getStringCellValue(),company_id);
                
                LineOfBusiness lob_data = this.checkForLOB(org_data,row.getCell(3).getStringCellValue(), row.getCell(4).getStringCellValue(),  row.getCell(5).getStringCellValue());

                BusinessFunction bf_data = this.checkForBF(lob_data, row.getCell(6).getStringCellValue(), row.getCell(7).getStringCellValue(), row.getCell(8).getStringCellValue(), row.getCell(9).getStringCellValue());

                Capabilities cap_data = this.checkForCap(bf_data, row.getCell(10).getStringCellValue());

                if(row.getCell(11).getStringCellValue() != "") {
                    BusinessProcess bp_data = this.checkForBP(cap_data, row.getCell(11).getStringCellValue(),
                    row.getCell(12).getDateCellValue(),row.getCell(13).getDateCellValue(), row.getCell(14).getStringCellValue());

                    if(row.getCell(15).getStringCellValue() != "") {
                        this.checkForActivity(bp_data,row.getCell(15).getStringCellValue(),
                        row.getCell(16).getStringCellValue());
                    }
                }
   
                result = "sucessfully Added";
            
            workbook.close();
            }
        }
        catch(IOException exception) {
            result = "Please check your input data";
        }
        
        return result;
    }

    @Override
    public String appPortfolioUpload(Long company_id, MultipartFile readExcelDataFile) {
        String response = " ";
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(readExcelDataFile.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);

            for (int i = 2; i < worksheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = worksheet.getRow(i);
                OraganizationalUnit org_data = this.checkForOrganizationalUnit(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue(), row.getCell(2).getStringCellValue(),company_id);
                
                LineOfBusiness lob_data = this.checkForLOB(org_data,row.getCell(3).getStringCellValue(), row.getCell(4).getStringCellValue(), row.getCell(5).getStringCellValue());

                Application app_data = this.checkForApplication(lob_data,row.getCell(6).getStringCellValue(), row.getCell(7).getStringCellValue(), row.getCell(8).getStringCellValue(), row.getCell(9).getDateCellValue());

                if(row.getCell(10).getStringCellValue() != "") {
                    TechnologyStack techStack_data = this.checkForTechnologyStack(row.getCell(10).getStringCellValue(),row.getCell(11).getStringCellValue(), row.getCell(12).getStringCellValue());

                    Technology tech_data = this.checkForTechnology(app_data, techStack_data);
    
                    if(tech_data.getTechnologyStack().getType().equals("CLOUD")) {
                        this.checkForSpend("INFRA",tech_data.getId(), (int) Math.round(row.getCell(13).getNumericCellValue()),row.getCell(14).getStringCellValue());
                    } else {
                        this.checkForSpend("LICENSE",tech_data.getId(), (int) Math.round(row.getCell(13).getNumericCellValue()),row.getCell(14).getStringCellValue()); 
                    }
                }

                if(row.getCell(15).getStringCellValue() != "") {
                    Expenditure expenditure_data = this.checkForExpenditure(app_data, row.getCell(15).getStringCellValue(),row.getCell(16).getStringCellValue(),
                    row.getCell(19).getDateCellValue(),row.getCell(20).getDateCellValue());
                    
                    this.checkForSpend(expenditure_data.getExpenditureType(), expenditure_data.getId(), 
                    (int) Math.round(row.getCell(17).getNumericCellValue()),row.getCell(18).getStringCellValue());
                }

                if((int) Math.round(row.getCell(21).getNumericCellValue()) == 0 && 
                row.getCell(22).getStringCellValue() != "") {
                    this.checkForBudget(app_data, (int) Math.round(row.getCell(21).getNumericCellValue()),row.getCell(22).getStringCellValue(),
                    (int) Math.round(row.getCell(21).getNumericCellValue()));
                }


               response = "sucessfully Added";
            
            workbook.close();
            }
        }
        catch(IOException exception) {
            response = "Please check your input data";
        }
        
        return response;
    }

    @Override
    public OraganizationalUnit checkForOrganizationalUnit(String orgName, String employee_id, String employee_name, Long company_id) {
        List<OraganizationalUnit> orgData = oraganizationalUnitRepository.findAll();
        OraganizationalUnit organizationData = new OraganizationalUnit();
        Boolean status = false;
        //For checking id already exist
        for(OraganizationalUnit organization : orgData) {
            if(organization.getCompany().getId() == company_id && 
            organization.getName().toLowerCase().equals(orgName.toLowerCase())) {
                if(organization.getEmployee().getEmployeeId() != employee_id) {
                    Employee employee = this.checkForEmployee(employee_id, employee_name);
                    organization.setEmployee(employee);
                    organizationData =  oraganizationalUnitRepository.save(organization);
                }
                else {
                    organizationData = organization;
                }
                status = true;
            }
        }
        /// if not exists then add to the table
        if(status == false) {
            OraganizationalUnit newOrgData = new OraganizationalUnit();
            newOrgData.setName(orgName);

            List<Company> companyObject = companyRepository.findAll();
            for(Company company : companyObject) {
                if(company.getId() == company_id) {
                    newOrgData.setCompany(company);
                }
            }
            newOrgData.setEmployee(this.checkForEmployee(employee_id, employee_name));
            organizationData =  oraganizationalUnitRepository.save(newOrgData);
        }
        return organizationData;
    }

    @Override
    public LineOfBusiness checkForLOB(OraganizationalUnit orgData,String lobName, String employee_id, String employee_name) {
        List<LineOfBusiness> LOBData = lineOfBusinessRepository.findAll();
        LineOfBusiness lineOfBusiness = new LineOfBusiness();
        Boolean status = false;
        //For checking id already exist
        for(LineOfBusiness lob : LOBData) {
            if(lob.getOraganizationalUnit().getCompany().getId() == orgData.getCompany().getId() && 
            lob.getName().toLowerCase().equals(lobName.toLowerCase())) {   
                if(lob.getEmployee().getEmployeeId() != employee_id) {
                    Employee employee = this.checkForEmployee(employee_id, employee_name);         
                    lob.setEmployee(employee);
                    lineOfBusiness =  lineOfBusinessRepository.save(lob);
                }
                else {
                    lineOfBusiness = lob;
                }
                status = true;
            }
        }
        // if not exists then add to the table
        if(status == false) {
            LineOfBusiness newLobData = new LineOfBusiness();
            newLobData.setName(lobName);
            newLobData.setOraganizationalUnit(orgData);
            newLobData.setEmployee(this.checkForEmployee(employee_id, employee_name));
            lineOfBusiness =  lineOfBusinessRepository.save(newLobData);
        }
        return lineOfBusiness;
    }

    @Override
    public BusinessFunction checkForBF(LineOfBusiness lobData,
     String bfName, String bfType, String employee_id, String employee_name) {
        List<BusinessFunction> bfData = businessFunctionRepository.findAll();
        BusinessFunction businessFunction = new BusinessFunction();
        Boolean status = false;
        //For checking id already exist
        for(BusinessFunction bf : bfData) {
            if(bf.getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == lobData.getOraganizationalUnit().getCompany().getId() && 
            bf.getName().toLowerCase().equals(bfName.toLowerCase())) {
                if(bf.getEmployee().getEmployeeId() != employee_id) {
                    Employee employee = this.checkForEmployee(employee_id, employee_name);
                    bf.setEmployee(employee);
                    businessFunction = businessFunctionRepository.save(bf);
                }
                else {
                    businessFunction = bf;
                }
                status = true;
            }
        }
        // if not exists then add to the table
        if(status == false) {
            BusinessFunction newbfData = new BusinessFunction();
            newbfData.setName(bfName);
            newbfData.setLineOfBusiness(lobData);
            newbfData.setType(BusinessFunctionType.valueOf(bfType));

            newbfData.setEmployee(this.checkForEmployee(employee_id, employee_name));
            businessFunction =  businessFunctionRepository.save(newbfData);
        }
        return businessFunction;
    }

    @Override
    public Capabilities checkForCap(BusinessFunction bfData, String capName) {
        List<Capabilities> capData = capabilitiesRepository.findAll();
        Capabilities capability = new Capabilities();
        Boolean status = false;
        //For checking id already exist
        for(Capabilities cap : capData) {
            if(cap.getBusinessFunction().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == bfData.getLineOfBusiness().getOraganizationalUnit().getCompany().getId() && 
            cap.getDescription().toLowerCase().equals(capName.toLowerCase())) {
                capability = cap;
                status = true;
            }
        }
        // if not exists then add to the table
        if(status == false) {
            Capabilities newCapData = new Capabilities();
            newCapData.setDescription(capName);
            newCapData.setBusinessFunction(bfData);

            capability=  capabilitiesRepository.save(newCapData);
        }
        return capability;
    }

    @Override
    public BusinessProcess checkForBP(Capabilities capData, String bpName, Date startDate, Date endDate, String bpStatus) {
        List<BusinessProcess> bpData = businessProcessRepository.findAll();
        BusinessProcess businessProcess = new BusinessProcess();
        Boolean status = false;
        //For checking id already exist
        for(BusinessProcess bp : bpData) {
            if(bp.getCapabilities().getBusinessFunction().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == capData.getBusinessFunction().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() && 
            bp.getName().toLowerCase().equals(bpName.toLowerCase())) {
                businessProcess = bp;
                status = true;
            }
        }
        // if not exists then add to the table
        if(status == false) {
            BusinessProcess newBpData = new BusinessProcess();
            newBpData.setName(bpName);
            newBpData.setCapabilities(capData);
            newBpData.setStartDate(startDate.toInstant().atZone(defaultZoneId).toLocalDate());
            newBpData.setEndDate(endDate.toInstant().atZone(defaultZoneId).toLocalDate());
            newBpData.setStatus(ProcessStatus.valueOf(bpStatus));

            businessProcess = businessProcessRepository.save(newBpData);
        }
        return businessProcess;
    }

    @Override
    public Activity checkForActivity(BusinessProcess bpData, String name, String resourcesRequired) {
        List<Activity> activityData = activityRepository.findAll();
        Activity activity = new Activity();
        Boolean status = false;
        //For checking id already exist
        for(Activity data : activityData) {
            if(data.getBusinessProcess().getCapabilities().getBusinessFunction().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == bpData.getCapabilities().getBusinessFunction().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() && 
            data.getName().toLowerCase().equals(name.toLowerCase())) {
                activity= data;
                status = true;
            }
        }
        // if not exists then add to the table
        if(status == false) {
            Activity newActivityData = new Activity();
            newActivityData.setName(name);
            newActivityData.setBusinessProcess(bpData);
            newActivityData.setResourcesRequired(ResourcesRequired.valueOf(resourcesRequired));

            activity = activityRepository.save(newActivityData);
            activitySearchRepository.save(newActivityData);
        }
        return activity;
    }

    @Override
    public Employee checkForEmployee(String employee_id, String employee_name) {
        List<Employee> employeeData = employeeRepository.findAll();
        Employee employee = new Employee();
        Boolean status = false;
        //For checking id already exist
        for(Employee data : employeeData) {
            if(data.getEmployeeId() == employee_id) {
                employee = data;
                status = true;
            }
        }
        // if not exists then add to the table
        if(status == false) {
            Employee newEmployeeData = new Employee();
            newEmployeeData.setEmployeeId(employee_id);
            newEmployeeData.setName(employee_name);

            employee = employeeRepository.save(newEmployeeData);
        }
        return employee;
    } 

    @Override
    public Application checkForApplication(LineOfBusiness lob_data, String app_name, String app_type, String app_status, Date imp_date) {
        List<Application> applicationData = applicationRepository.findAll();
        Application application = new Application();
        Boolean status = false;
        //For checking id already exist
        for(Application data : applicationData) {
            if(data.getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == lob_data.getOraganizationalUnit().getCompany().getId() && 
            data.getName().toLowerCase().equals(app_name.toLowerCase())) {
                application= data;
                status = true;
            }
        }
        // if not exists then add to the table
        if(status == false) {
            Application newApplicationData = new Application();
            newApplicationData.setName(app_name);
	    newApplicationData.setDescription(app_name+"-"+ApplicationType.valueOf(app_type).toString());
            newApplicationData.setLineOfBusiness(lob_data);
            newApplicationData.setStatus(ApplicationStatus.valueOf(app_status));
            newApplicationData.setType(ApplicationType.valueOf(app_type));
            newApplicationData.setImplementationDate(imp_date.toInstant().atZone(defaultZoneId).toLocalDate());
            application = applicationRepository.save(newApplicationData);
            applicationSearchRepository.save(newApplicationData);
        }
        return application;
    } 
    
    @Override
    public TechnologyStack checkForTechnologyStack(String techName,String version, String techType) {
        List<TechnologyStack> technologystackData = technologyStackRepository.findAll();
        TechnologyStack technologyStack = new TechnologyStack();
        Boolean status = false;
        //For checking id already exist
        for(TechnologyStack data : technologystackData) {
            if(data.getName().toLowerCase().equals(techName.toLowerCase()+"-"+version) && 
            data.getType().toUpperCase().equals(techType.toUpperCase())) {
                technologyStack = data;
                status = true;
            }
        }
        // if not exists then add to the table
        if(status == false) {
            TechnologyStack newTechnologyStackdata = new TechnologyStack();
            newTechnologyStackdata.setName(techName+"-"+version);
            newTechnologyStackdata.setType(techType);
        
            technologyStack = technologyStackRepository.save(newTechnologyStackdata);
        }
        return technologyStack;
    }

    @Override
    public Technology checkForTechnology(Application app_data, TechnologyStack techStack_data) {
        List<Technology> technologyData = technologyRepository.findAll();
        Technology technology = new Technology();
        Boolean status = false;
        //For checking id already exist
        for(Technology techdata : technologyData) {
            if(techdata.getApplication().getId() == app_data.getId() && 
            techdata.getTechnologyStack().getId() == techStack_data.getId()) {
                technology = techdata;
                status = true;
            }
        }
        // if not exists then add to the table
        if(status == false) {
            Technology newTechnologydata = new Technology();
            newTechnologydata.setTechnologyStack(techStack_data);
            newTechnologydata.setApplication(app_data);
        
            technology = technologyRepository.save(newTechnologydata);
            technologySearchRepository.save(newTechnologydata);
        }
        return technology;
    }

    @Override
    public void checkForSpend(String type,Long spend_id, Integer amount, String successor){
        Date toDay = new Date();
        List<Spend> spendData = spendRepository.findAll();
        Spend spend = new Spend();
        Boolean status = false;
        //For checking id already exist
        for(Spend spend_data : spendData) {
            if(spend_data.getSpendId() == spend_id && spend_data.getExpenditureType().equals(type)) {
                spend = spend_data;
                if(spend.getAmount() != amount) {
                    spend.setAmount(amount);
                    spend.setSuccessor(CurrencySuccessor.valueOf(successor));
                    spendRepository.save(spend);
                }
                status = true;
            }
        }
        // if not exists then add to the table
        if(status == false) {
            Spend newSpendData = new Spend();
            newSpendData.setAmount(amount);
            newSpendData.setSuccessor(CurrencySuccessor.valueOf(successor));
            newSpendData.setExpenditureType(type);
            newSpendData.setSpendId(spend_id);
            newSpendData.setDateOfUpdate(toDay.toInstant().atZone(defaultZoneId).toLocalDate());
     
            spend = spendRepository.save(newSpendData);
        } 
    }

    @Override
    public Expenditure checkForExpenditure(Application appData,String exp_type, String exp_desc, Date startDate, Date end_date) {
        List<Expenditure> expData = expenditureRepository.findAll();
        Expenditure expenditure = new Expenditure();
        Boolean status = false;
        //For checking id already exist
        for(Expenditure expd : expData) {
            if(expd.getApplication().getId() == appData.getId() && 
            expd.getExpenditureType().equals(exp_type)) {
                expenditure = expd;
                status = true;
            }
        }
        // if not exists then add to the table
        if(status == false) {
            Expenditure newExpendituredata = new Expenditure();
            newExpendituredata.setDescription(exp_desc);
            newExpendituredata.setExpenditureType(exp_type);
            newExpendituredata.setStartDate(startDate.toInstant().atZone(defaultZoneId).toLocalDate());
            newExpendituredata.setEndDate(end_date.toInstant().atZone(defaultZoneId).toLocalDate());
            newExpendituredata.setApplication(appData);
        
            expenditure = expenditureRepository.save(newExpendituredata);
        }
        return expenditure;
    }

    @Override
    public void checkForBudget(Application app_data, Integer amount, String successor, Integer year) {
        List<Budget> budgetData = budgetRepository.findAll();
        Budget budget = new Budget();
        Boolean status = false;
        //For checking id already exist
        for(Budget data : budgetData) {
            if(data.getApplication().getId() == app_data.getId() && 
            data.getYear() == year) {
                budget = data;
                if(data.getAmount() != amount) {
                    budget.setAmount(amount);
                    budget.setSuccessor(CurrencySuccessor.valueOf(successor));
                    budgetRepository.save(budget);
                }
                
                status = true;
            }
        }
        // if not exists then add to the table
        if(status == false) {
            Budget newBudgetData = new Budget();
            newBudgetData.setAmount(amount);
            newBudgetData.setApplication(app_data);
            newBudgetData.setSuccessor(CurrencySuccessor.valueOf(successor));
            newBudgetData.setYear(year);
   
            budgetRepository.save(newBudgetData);
        }
    }
}
