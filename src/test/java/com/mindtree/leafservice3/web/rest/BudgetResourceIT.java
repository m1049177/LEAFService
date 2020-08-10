package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Budget;
import com.mindtree.leafservice3.domain.Application;
import com.mindtree.leafservice3.repository.BudgetRepository;
import com.mindtree.leafservice3.repository.search.BudgetSearchRepository;
import com.mindtree.leafservice3.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static com.mindtree.leafservice3.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mindtree.leafservice3.domain.enumeration.CurrencySuccessor;
/**
 * Integration tests for the {@link BudgetResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class BudgetResourceIT {

    private static final Integer DEFAULT_AMOUNT = 1;
    private static final Integer UPDATED_AMOUNT = 2;

    private static final Integer DEFAULT_YEAR = 1;
    private static final Integer UPDATED_YEAR = 2;

    private static final CurrencySuccessor DEFAULT_SUCCESSOR = CurrencySuccessor.M;
    private static final CurrencySuccessor UPDATED_SUCCESSOR = CurrencySuccessor.K;

    @Autowired
    private BudgetRepository budgetRepository;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.BudgetSearchRepositoryMockConfiguration
     */
    @Autowired
    private BudgetSearchRepository mockBudgetSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restBudgetMockMvc;

    private Budget budget;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BudgetResource budgetResource = new BudgetResource(budgetRepository, mockBudgetSearchRepository);
        this.restBudgetMockMvc = MockMvcBuilders.standaloneSetup(budgetResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Budget createEntity(EntityManager em) {
        Budget budget = new Budget()
            .amount(DEFAULT_AMOUNT)
            .year(DEFAULT_YEAR)
            .successor(DEFAULT_SUCCESSOR);
        // Add required entity
        Application application;
        if (TestUtil.findAll(em, Application.class).isEmpty()) {
            application = ApplicationResourceIT.createEntity(em);
            em.persist(application);
            em.flush();
        } else {
            application = TestUtil.findAll(em, Application.class).get(0);
        }
        budget.setApplication(application);
        return budget;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Budget createUpdatedEntity(EntityManager em) {
        Budget budget = new Budget()
            .amount(UPDATED_AMOUNT)
            .year(UPDATED_YEAR)
            .successor(UPDATED_SUCCESSOR);
        // Add required entity
        Application application;
        if (TestUtil.findAll(em, Application.class).isEmpty()) {
            application = ApplicationResourceIT.createUpdatedEntity(em);
            em.persist(application);
            em.flush();
        } else {
            application = TestUtil.findAll(em, Application.class).get(0);
        }
        budget.setApplication(application);
        return budget;
    }

    @BeforeEach
    public void initTest() {
        budget = createEntity(em);
    }

    @Test
    @Transactional
    public void createBudget() throws Exception {
        int databaseSizeBeforeCreate = budgetRepository.findAll().size();

        // Create the Budget
        restBudgetMockMvc.perform(post("/api/budgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budget)))
            .andExpect(status().isCreated());

        // Validate the Budget in the database
        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeCreate + 1);
        Budget testBudget = budgetList.get(budgetList.size() - 1);
        assertThat(testBudget.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testBudget.getYear()).isEqualTo(DEFAULT_YEAR);
        assertThat(testBudget.getSuccessor()).isEqualTo(DEFAULT_SUCCESSOR);

        // Validate the Budget in Elasticsearch
        verify(mockBudgetSearchRepository, times(1)).save(testBudget);
    }

    @Test
    @Transactional
    public void createBudgetWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = budgetRepository.findAll().size();

        // Create the Budget with an existing ID
        budget.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBudgetMockMvc.perform(post("/api/budgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budget)))
            .andExpect(status().isBadRequest());

        // Validate the Budget in the database
        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeCreate);

        // Validate the Budget in Elasticsearch
        verify(mockBudgetSearchRepository, times(0)).save(budget);
    }


    @Test
    @Transactional
    public void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = budgetRepository.findAll().size();
        // set the field null
        budget.setAmount(null);

        // Create the Budget, which fails.

        restBudgetMockMvc.perform(post("/api/budgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budget)))
            .andExpect(status().isBadRequest());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkYearIsRequired() throws Exception {
        int databaseSizeBeforeTest = budgetRepository.findAll().size();
        // set the field null
        budget.setYear(null);

        // Create the Budget, which fails.

        restBudgetMockMvc.perform(post("/api/budgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budget)))
            .andExpect(status().isBadRequest());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSuccessorIsRequired() throws Exception {
        int databaseSizeBeforeTest = budgetRepository.findAll().size();
        // set the field null
        budget.setSuccessor(null);

        // Create the Budget, which fails.

        restBudgetMockMvc.perform(post("/api/budgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budget)))
            .andExpect(status().isBadRequest());

        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBudgets() throws Exception {
        // Initialize the database
        budgetRepository.saveAndFlush(budget);

        // Get all the budgetList
        restBudgetMockMvc.perform(get("/api/budgets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(budget.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].successor").value(hasItem(DEFAULT_SUCCESSOR.toString())));
    }
    
    @Test
    @Transactional
    public void getBudget() throws Exception {
        // Initialize the database
        budgetRepository.saveAndFlush(budget);

        // Get the budget
        restBudgetMockMvc.perform(get("/api/budgets/{id}", budget.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(budget.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.year").value(DEFAULT_YEAR))
            .andExpect(jsonPath("$.successor").value(DEFAULT_SUCCESSOR.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBudget() throws Exception {
        // Get the budget
        restBudgetMockMvc.perform(get("/api/budgets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBudget() throws Exception {
        // Initialize the database
        budgetRepository.saveAndFlush(budget);

        int databaseSizeBeforeUpdate = budgetRepository.findAll().size();

        // Update the budget
        Budget updatedBudget = budgetRepository.findById(budget.getId()).get();
        // Disconnect from session so that the updates on updatedBudget are not directly saved in db
        em.detach(updatedBudget);
        updatedBudget
            .amount(UPDATED_AMOUNT)
            .year(UPDATED_YEAR)
            .successor(UPDATED_SUCCESSOR);

        restBudgetMockMvc.perform(put("/api/budgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBudget)))
            .andExpect(status().isOk());

        // Validate the Budget in the database
        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeUpdate);
        Budget testBudget = budgetList.get(budgetList.size() - 1);
        assertThat(testBudget.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testBudget.getYear()).isEqualTo(UPDATED_YEAR);
        assertThat(testBudget.getSuccessor()).isEqualTo(UPDATED_SUCCESSOR);

        // Validate the Budget in Elasticsearch
        verify(mockBudgetSearchRepository, times(1)).save(testBudget);
    }

    @Test
    @Transactional
    public void updateNonExistingBudget() throws Exception {
        int databaseSizeBeforeUpdate = budgetRepository.findAll().size();

        // Create the Budget

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBudgetMockMvc.perform(put("/api/budgets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(budget)))
            .andExpect(status().isBadRequest());

        // Validate the Budget in the database
        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Budget in Elasticsearch
        verify(mockBudgetSearchRepository, times(0)).save(budget);
    }

    @Test
    @Transactional
    public void deleteBudget() throws Exception {
        // Initialize the database
        budgetRepository.saveAndFlush(budget);

        int databaseSizeBeforeDelete = budgetRepository.findAll().size();

        // Delete the budget
        restBudgetMockMvc.perform(delete("/api/budgets/{id}", budget.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Budget> budgetList = budgetRepository.findAll();
        assertThat(budgetList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Budget in Elasticsearch
        verify(mockBudgetSearchRepository, times(1)).deleteById(budget.getId());
    }

    @Test
    @Transactional
    public void searchBudget() throws Exception {
        // Initialize the database
        budgetRepository.saveAndFlush(budget);
        when(mockBudgetSearchRepository.search(queryStringQuery("id:" + budget.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(budget), PageRequest.of(0, 1), 1));
        // Search the budget
        restBudgetMockMvc.perform(get("/api/_search/budgets?query=id:" + budget.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(budget.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
            .andExpect(jsonPath("$.[*].year").value(hasItem(DEFAULT_YEAR)))
            .andExpect(jsonPath("$.[*].successor").value(hasItem(DEFAULT_SUCCESSOR.toString())));
    }
}
