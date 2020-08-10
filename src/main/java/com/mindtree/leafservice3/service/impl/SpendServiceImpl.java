package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.SpendService;
import com.mindtree.leafservice3.service.dto.ApplicationData;
import com.mindtree.leafservice3.domain.Application;
import com.mindtree.leafservice3.domain.Budget;
import com.mindtree.leafservice3.domain.Expenditure;
import com.mindtree.leafservice3.domain.LineOfBusiness;
import com.mindtree.leafservice3.domain.Spend;
import com.mindtree.leafservice3.domain.Technology;
import com.mindtree.leafservice3.domain.enumeration.ApplicationStatus;
import com.mindtree.leafservice3.domain.enumeration.CurrencySuccessor;
import com.mindtree.leafservice3.repository.ApplicationRepository;
import com.mindtree.leafservice3.repository.BudgetRepository;
import com.mindtree.leafservice3.repository.ExpenditureRepository;
import com.mindtree.leafservice3.repository.LineOfBusinessRepository;
import com.mindtree.leafservice3.repository.SpendRepository;
import com.mindtree.leafservice3.repository.TechnologyRepository;
import com.mindtree.leafservice3.repository.search.SpendSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Spend}.
 */
@Service
@Transactional
public class SpendServiceImpl implements SpendService {

    private final Logger log = LoggerFactory.getLogger(SpendServiceImpl.class);

    private final SpendRepository spendRepository;

    private final SpendSearchRepository spendSearchRepository;

    private final ApplicationRepository applicationRepository;

    private final TechnologyRepository technologyRepository;

    private final ExpenditureRepository expenditureRepository;

    private final LineOfBusinessRepository lineOfBusinessRepository;

    private final BudgetRepository budgetRepository;

    private int positioin;

    public SpendServiceImpl(SpendRepository spendRepository, SpendSearchRepository spendSearchRepository,
            ApplicationRepository applicationRepository, TechnologyRepository technologyRepository,
            ExpenditureRepository expenditureRepository, LineOfBusinessRepository lineOfBusinessRepository,
            BudgetRepository budgetRepository) {
        this.spendRepository = spendRepository;
        this.spendSearchRepository = spendSearchRepository;
        this.applicationRepository = applicationRepository;
        this.technologyRepository = technologyRepository;
        this.expenditureRepository = expenditureRepository;
        this.lineOfBusinessRepository = lineOfBusinessRepository;
        this.budgetRepository = budgetRepository;
    }

    /**
     * Save a spend.
     *
     * @param spend the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Spend save(Spend spend) {
        log.debug("Request to save Spend : {}", spend);
        Spend result = spendRepository.save(spend);
        spendSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the spends.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Spend> findAll() {
        log.debug("Request to get all Spends");
        return spendRepository.findAll();
    }

    /**
     * Get one spend by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Spend> findOne(Long id) {
        log.debug("Request to get Spend : {}", id);
        return spendRepository.findById(id);
    }

    /**
     * Delete the spend by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Spend : {}", id);
        spendRepository.deleteById(id);
        spendSearchRepository.deleteById(id);
    }

    /**
     * Search for the spend corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Spend> search(String query) {
        log.debug("Request to search Spends for query {}", query);
        return StreamSupport.stream(spendSearchRepository.search(queryStringQuery(query)).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<Spend> getSpendData(Long company_id) {
        log.debug("Request to get all spend data based on company id");
        List<Spend> spendData = spendRepository.findAll();
        List<Technology> technologies = technologyRepository.findAll();
        List<Expenditure> expenditures = expenditureRepository.findAll();
        List<Spend> spends = new ArrayList<>();

        List<Technology> technologyData = technologies.stream()
                                            .filter(value -> value.getApplication().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == company_id)
                                            .collect(Collectors.toList());

        List<Expenditure> expenditureData = expenditures.stream()
                                            .filter(value -> value.getApplication().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == company_id)
                                            .collect(Collectors.toList());

        for(Spend spend : spendData) {
                if(spend.getExpenditureType().equals("INFRA") || spend.getExpenditureType().equals("LICENSE")) {
                    for(Technology technology : technologyData) {
                        if(spend.getSpendId() == (technology.getId())) {
                            spends.add(spend);
                        }
                    }
                }
                else {
                    for(Expenditure expenditure : expenditureData) {
                        if(spend.getSpendId() == (expenditure.getId())) {
                            spends.add(spend);
                        }
                    }
                }  
            }
        return spends;
    }


    @Override
    public List<ApplicationData> getApplicationData(Long company_id) {
        List<ApplicationData> applicationData = new ArrayList<>();
        List<Technology> technologies = technologyRepository.findAll();
        List<Expenditure> expenditures = expenditureRepository.findAll();

        List<Technology> technologyData = technologies.stream()
                                            .filter(value -> value.getApplication().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == company_id)
                                            .collect(Collectors.toList());

        List<Expenditure> expenditureData = expenditures.stream()
                                            .filter(value -> value.getApplication().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == company_id)
                                            .collect(Collectors.toList());
        
        for(Technology technology : technologyData) {
            if(technology.getTechnologyStack().getType().equals("CLOUD")) {
                ApplicationData techData = new ApplicationData();
                    techData.status = technology.getApplication().getStatus().toString();
                    techData.data = technology;
                    techData.type = "INFRA";
                applicationData.add(techData);
            }
            else {
                ApplicationData techData = new ApplicationData();
                    techData.status = technology.getApplication().getStatus().toString();
                    techData.data = technology;
                    techData.type = "LICENSE";  
                applicationData.add(techData);             
            }
        }

        for(Expenditure expenditure : expenditureData) {
            ApplicationData expData = new ApplicationData();
                expData.status = expenditure.getApplication().getStatus().toString();
                expData.data = expenditure;
                expData.type = expenditure.getExpenditureType();  
            applicationData.add(expData);              
        }
    
        return applicationData;
    }

    @Override
    public List<Object> findYearlySpendDetails(Long company_id) {
        // check();
        List<Object> result = new ArrayList<>();
        List<Integer> runningAppResult = new ArrayList<>();
        List<Integer> growingAppResult = new ArrayList<>();
        List<Integer> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Spend> spends = this.getSpendData(company_id);

        List<Technology> technologies = technologyRepository.findAll();
        List<Expenditure> expendictures = expenditureRepository.findAll();
        List<LineOfBusiness> lineOfBusinesses = lineOfBusinessRepository.findAll();
        List<Budget> budgets = budgetRepository.findAll();

        /// Filtering based on company_id

        List<Technology> technologyData = technologies.stream()
                                            .filter(value-> value.getApplication().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == company_id)
                                            .collect(Collectors.toList());

        List<Expenditure> expenditureData = expendictures.stream()
                                                .filter(value-> value.getApplication().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == company_id)
                                                .collect(Collectors.toList());

        List<Budget> budgetData = budgets.stream()
                                    .filter(value-> value.getApplication().getLineOfBusiness().getOraganizationalUnit().getCompany().getId() == company_id)
                                    .collect(Collectors.toList());

        List<LineOfBusiness> lineOfBusinessData = lineOfBusinesses.stream()
                                                        .filter(value-> value.getOraganizationalUnit().getCompany().getId() == company_id)
                                                        .collect(Collectors.toList());
        List<String> lobNames = new ArrayList<>();
        List<Integer> currentYearBudget = new ArrayList<>();
        List<Integer> currentYearSpend = new ArrayList<>();

        for (LineOfBusiness linBusiness : lineOfBusinessData) {
            lobNames.add(linBusiness.getName());
            currentYearSpend.add(0);
            currentYearBudget.add(0);
        }
        int count = 0;
        for (int year = currentYear; count < 5; year--, count++) {
            years.add(year);
            int runningTotal = 0;
            int growingTotal = 0;
            for (Spend spend : spends) {
                for (Technology technology : technologyData) {
                    if ((spend.getSpendId() == (technology.getId()) && year == spend.getDateOfUpdate().getYear())
                            && ((technology.getTechnologyStack().getType().equals("CLOUD")
                                    && spend.getExpenditureType().equals("INFRA"))
                                    || (technology.getTechnologyStack().getType() != "CLOUD"
                                            && spend.getExpenditureType().equals("LICENSE")))) {
                        if (technology.getApplication().getStatus() == ApplicationStatus.Running) {
                            if(spend.getSuccessor() == CurrencySuccessor.K) {
                                runningTotal = runningTotal + spend.getAmount();
                            } else if(spend.getSuccessor() == CurrencySuccessor.M) {
                                runningTotal = runningTotal + spend.getAmount() * 1000;
                            } else {
                                runningTotal = runningTotal + spend.getAmount();
                            } 
                        }
                        if (technology.getApplication().getStatus() == ApplicationStatus.InProgress) {
                            if(spend.getSuccessor() == CurrencySuccessor.K) {
                                growingTotal = growingTotal + spend.getAmount();
                            } else if(spend.getSuccessor() == CurrencySuccessor.M) {
                                growingTotal = growingTotal + spend.getAmount() * 1000;
                            } else {
                                growingTotal = growingTotal + spend.getAmount();
                            } 
                        }
                        if (currentYear == year) {
                            positioin = 0;
                            lobNames.forEach(el -> {
                                if (el.equals(technology.getApplication().getLineOfBusiness().getName())) {
                                    if(spend.getSuccessor() == CurrencySuccessor.K) {
                                        currentYearSpend.set(positioin,
                                        currentYearSpend.get(positioin) + (spend.getAmount()));
                                    } else if(spend.getSuccessor() == CurrencySuccessor.M) {
                                        currentYearSpend.set(positioin,
                                        currentYearSpend.get(positioin) + (spend.getAmount() * 1000));
                                    } else {
                                        currentYearSpend.set(positioin,
                                        currentYearSpend.get(positioin) + (spend.getAmount()));
                                    } 
                                    currentYearSpend.set(positioin,
                                            currentYearSpend.get(positioin) + spend.getAmount());
                                }
                                positioin++;
                            });
                            // currentYearSpend.forEach((lobName, totalSpend) -> {
                            // if
                            // (lobName.equals(technology.getApplication().getLineOfBusiness().getName())) {
                            // totalSpend = totalSpend + spend.getAmount();
                            // currentYearSpend.put(lobName, totalSpend);
                            // }
                            // });
                        }
                    }
                }
                for (Expenditure expenditure : expenditureData) {
                    if (spend.getSpendId() == (expenditure.getId()) && year == spend.getDateOfUpdate().getYear()
                            && (spend.getExpenditureType() != "LICENSE"
                                    || spend.getExpenditureType() != "INFRA")) {
                        if (expenditure.getApplication().getStatus() == ApplicationStatus.Running) {
                            if(spend.getSuccessor() == CurrencySuccessor.K) {
                                runningTotal = runningTotal + spend.getAmount();
                            } else if(spend.getSuccessor() == CurrencySuccessor.M) {
                                runningTotal = runningTotal + spend.getAmount() * 1000;
                            } else {
                                runningTotal = runningTotal + spend.getAmount();
                            } 
                        }
                        if (expenditure.getApplication().getStatus() == ApplicationStatus.InProgress) {
                            if(spend.getSuccessor() == CurrencySuccessor.K) {
                                growingTotal = growingTotal + spend.getAmount();
                            } else if(spend.getSuccessor() == CurrencySuccessor.M) {
                                growingTotal = growingTotal + spend.getAmount() * 1000;
                            } else {
                                growingTotal = growingTotal + spend.getAmount();
                            } 
                        }
                        if (currentYear == year) {
                            positioin = 0;
                            for (String lob : lobNames) {
                                if (lob.equals(expenditure.getApplication().getLineOfBusiness().getName())) {
                                    if(spend.getSuccessor() == CurrencySuccessor.K) {
                                        currentYearSpend.set(positioin,
                                        currentYearSpend.get(positioin) + (spend.getAmount()));
                                    } else if(spend.getSuccessor() == CurrencySuccessor.M) {
                                        currentYearSpend.set(positioin,
                                        currentYearSpend.get(positioin) + (spend.getAmount() * 1000));
                                    } else {
                                        currentYearSpend.set(positioin,
                                        currentYearSpend.get(positioin) + (spend.getAmount()));
                                    } 
                                }
                                positioin++;
                            }
                        }
                    }
                }
            }
            runningAppResult.add(runningTotal);
            growingAppResult.add(growingTotal);
        }
        positioin = 0;
        lobNames.forEach(lob -> {
            for (Budget budget : budgetData) {
                if (budget.getYear() == currentYear
                        && lob.equals(budget.getApplication().getLineOfBusiness().getName())) {
                            if(budget.getSuccessor() == CurrencySuccessor.K) {
                                currentYearBudget.set(positioin,
                            (int) ((int) currentYearBudget.get(positioin) + budget.getAmount()));
                            } else if(budget.getSuccessor() == CurrencySuccessor.M) {
                                currentYearBudget.set(positioin,
                            (int) ((int) currentYearBudget.get(positioin) + budget.getAmount() * 1000));
                            } else {
                                currentYearBudget.set(positioin,
                            (int) ((int) currentYearBudget.get(positioin) + budget.getAmount()));
                            }     
                }
            }           
            positioin++;
        });
        result.add(years);
        result.add(runningAppResult);
        result.add(growingAppResult);
        result.add(lobNames);
        result.add(currentYearSpend);
        result.add(currentYearBudget);
        return result;
    }

}
