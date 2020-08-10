package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Spend;
import com.mindtree.leafservice3.repository.SpendRepository;
import com.mindtree.leafservice3.repository.search.SpendSearchRepository;
import com.mindtree.leafservice3.service.SpendService;
import com.mindtree.leafservice3.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static com.mindtree.leafservice3.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mindtree.leafservice3.domain.enumeration.ExpenditureSubType;
import com.mindtree.leafservice3.domain.enumeration.CurrencySuccessor;
/**
 * Integration tests for the {@link SpendResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class SpendResourceIT {

    private static final LocalDate DEFAULT_DATE_OF_UPDATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_UPDATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_AMOUNT = 1;
    private static final Integer UPDATED_AMOUNT = 2;

    private static final Long DEFAULT_SPEND_ID = 1L;
    private static final Long UPDATED_SPEND_ID = 1L;

    private static final String DEFAULT_EXPENDITURE_TYPE = "INFRA";
    private static final String UPDATED_EXPENDITURE_TYPE = "LICENSE";

    private static final String DEFAULT_EXPENDITURE_SUB_TYPE = "ONCLOUD";
    private static final String UPDATED_EXPENDITURE_SUB_TYPE = "ONPREMISSES";

    private static final CurrencySuccessor DEFAULT_SUCCESSOR = CurrencySuccessor.NOSUCCESSOR;
    private static final CurrencySuccessor UPDATED_SUCCESSOR = CurrencySuccessor.K;

    @Autowired
    private SpendRepository spendRepository;

    @Autowired
    private SpendService spendService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.SpendSearchRepositoryMockConfiguration
     */
    @Autowired
    private SpendSearchRepository mockSpendSearchRepository;

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

    private MockMvc restSpendMockMvc;

    private Spend spend;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SpendResource spendResource = new SpendResource(spendService);
        this.restSpendMockMvc = MockMvcBuilders.standaloneSetup(spendResource)
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
    public static Spend createEntity(EntityManager em) {
        Spend spend = new Spend()
            .dateOfUpdate(DEFAULT_DATE_OF_UPDATE)
            .amount(DEFAULT_AMOUNT)
            .spendId(DEFAULT_SPEND_ID)
            .expenditureType(DEFAULT_EXPENDITURE_TYPE)
            .expenditureSubType(DEFAULT_EXPENDITURE_SUB_TYPE)
            .successor(DEFAULT_SUCCESSOR);
        return spend;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Spend createUpdatedEntity(EntityManager em) {
        Spend spend = new Spend()
            .dateOfUpdate(UPDATED_DATE_OF_UPDATE)
            .amount(UPDATED_AMOUNT)
            .spendId(UPDATED_SPEND_ID)
            .expenditureType(UPDATED_EXPENDITURE_TYPE)
            .expenditureSubType(UPDATED_EXPENDITURE_SUB_TYPE)
            .successor(UPDATED_SUCCESSOR);
        return spend;
    }

    @BeforeEach
    public void initTest() {
        spend = createEntity(em);
    }

    @Test
    @Transactional
    public void createSpend() throws Exception {
        int databaseSizeBeforeCreate = spendRepository.findAll().size();

        // Create the Spend
        restSpendMockMvc.perform(post("/api/spends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(spend)))
            .andExpect(status().isCreated());

        // Validate the Spend in the database
        List<Spend> spendList = spendRepository.findAll();
        assertThat(spendList).hasSize(databaseSizeBeforeCreate + 1);
        Spend testSpend = spendList.get(spendList.size() - 1);
        assertThat(testSpend.getDateOfUpdate()).isEqualTo(DEFAULT_DATE_OF_UPDATE);
        assertThat(testSpend.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testSpend.getSpendId()).isEqualTo(DEFAULT_SPEND_ID);
        assertThat(testSpend.getExpenditureType()).isEqualTo(DEFAULT_EXPENDITURE_TYPE);
        assertThat(testSpend.getExpenditureSubType()).isEqualTo(DEFAULT_EXPENDITURE_SUB_TYPE);
        assertThat(testSpend.getSuccessor()).isEqualTo(DEFAULT_SUCCESSOR);

        // Validate the Spend in Elasticsearch
        verify(mockSpendSearchRepository, times(1)).save(testSpend);
    }

    @Test
    @Transactional
    public void createSpendWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = spendRepository.findAll().size();

        // Create the Spend with an existing ID
        spend.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpendMockMvc.perform(post("/api/spends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(spend)))
            .andExpect(status().isBadRequest());

        // Validate the Spend in the database
        List<Spend> spendList = spendRepository.findAll();
        assertThat(spendList).hasSize(databaseSizeBeforeCreate);

        // Validate the Spend in Elasticsearch
        verify(mockSpendSearchRepository, times(0)).save(spend);
    }


    @Test
    @Transactional
    public void checkDateOfUpdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = spendRepository.findAll().size();
        // set the field null
        spend.setDateOfUpdate(null);

        // Create the Spend, which fails.

        restSpendMockMvc.perform(post("/api/spends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(spend)))
            .andExpect(status().isBadRequest());

        List<Spend> spendList = spendRepository.findAll();
        assertThat(spendList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = spendRepository.findAll().size();
        // set the field null
        spend.setAmount(null);

        // Create the Spend, which fails.

        restSpendMockMvc.perform(post("/api/spends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(spend)))
            .andExpect(status().isBadRequest());

        List<Spend> spendList = spendRepository.findAll();
        assertThat(spendList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSpendIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = spendRepository.findAll().size();
        // set the field null
        spend.setSpendId(null);

        // Create the Spend, which fails.

        restSpendMockMvc.perform(post("/api/spends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(spend)))
            .andExpect(status().isBadRequest());

        List<Spend> spendList = spendRepository.findAll();
        assertThat(spendList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkExpenditureTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = spendRepository.findAll().size();
        // set the field null
        spend.setExpenditureType(null);

        // Create the Spend, which fails.

        restSpendMockMvc.perform(post("/api/spends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(spend)))
            .andExpect(status().isBadRequest());

        List<Spend> spendList = spendRepository.findAll();
        assertThat(spendList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSuccessorIsRequired() throws Exception {
        int databaseSizeBeforeTest = spendRepository.findAll().size();
        // set the field null
        spend.setSuccessor(null);

        // Create the Spend, which fails.

        restSpendMockMvc.perform(post("/api/spends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(spend)))
            .andExpect(status().isBadRequest());

        List<Spend> spendList = spendRepository.findAll();
        assertThat(spendList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSpends() throws Exception {
        // Initialize the database
        spendRepository.saveAndFlush(spend);

        // Get all the spendList
        restSpendMockMvc.perform(get("/api/spends?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spend.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateOfUpdate").value(hasItem(DEFAULT_DATE_OF_UPDATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.[*].spendId").value(hasItem(DEFAULT_SPEND_ID)))
            .andExpect(jsonPath("$.[*].expenditureType").value(hasItem(DEFAULT_EXPENDITURE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].expenditureSubType").value(hasItem(DEFAULT_EXPENDITURE_SUB_TYPE.toString())))
            .andExpect(jsonPath("$.[*].successor").value(hasItem(DEFAULT_SUCCESSOR.toString())));
    }
    
    @Test
    @Transactional
    public void getSpend() throws Exception {
        // Initialize the database
        spendRepository.saveAndFlush(spend);

        // Get the spend
        restSpendMockMvc.perform(get("/api/spends/{id}", spend.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(spend.getId().intValue()))
            .andExpect(jsonPath("$.dateOfUpdate").value(DEFAULT_DATE_OF_UPDATE.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT))
            .andExpect(jsonPath("$.spendId").value(DEFAULT_SPEND_ID))
            .andExpect(jsonPath("$.expenditureType").value(DEFAULT_EXPENDITURE_TYPE.toString()))
            .andExpect(jsonPath("$.expenditureSubType").value(DEFAULT_EXPENDITURE_SUB_TYPE.toString()))
            .andExpect(jsonPath("$.successor").value(DEFAULT_SUCCESSOR.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSpend() throws Exception {
        // Get the spend
        restSpendMockMvc.perform(get("/api/spends/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSpend() throws Exception {
        // Initialize the database
        spendService.save(spend);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockSpendSearchRepository);

        int databaseSizeBeforeUpdate = spendRepository.findAll().size();

        // Update the spend
        Spend updatedSpend = spendRepository.findById(spend.getId()).get();
        // Disconnect from session so that the updates on updatedSpend are not directly saved in db
        em.detach(updatedSpend);
        updatedSpend
            .dateOfUpdate(UPDATED_DATE_OF_UPDATE)
            .amount(UPDATED_AMOUNT)
            .spendId(UPDATED_SPEND_ID)
            .expenditureType(UPDATED_EXPENDITURE_TYPE)
            .expenditureSubType(UPDATED_EXPENDITURE_SUB_TYPE)
            .successor(UPDATED_SUCCESSOR);

        restSpendMockMvc.perform(put("/api/spends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSpend)))
            .andExpect(status().isOk());

        // Validate the Spend in the database
        List<Spend> spendList = spendRepository.findAll();
        assertThat(spendList).hasSize(databaseSizeBeforeUpdate);
        Spend testSpend = spendList.get(spendList.size() - 1);
        assertThat(testSpend.getDateOfUpdate()).isEqualTo(UPDATED_DATE_OF_UPDATE);
        assertThat(testSpend.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testSpend.getSpendId()).isEqualTo(UPDATED_SPEND_ID);
        assertThat(testSpend.getExpenditureType()).isEqualTo(UPDATED_EXPENDITURE_TYPE);
        assertThat(testSpend.getExpenditureSubType()).isEqualTo(UPDATED_EXPENDITURE_SUB_TYPE);
        assertThat(testSpend.getSuccessor()).isEqualTo(UPDATED_SUCCESSOR);

        // Validate the Spend in Elasticsearch
        verify(mockSpendSearchRepository, times(1)).save(testSpend);
    }

    @Test
    @Transactional
    public void updateNonExistingSpend() throws Exception {
        int databaseSizeBeforeUpdate = spendRepository.findAll().size();

        // Create the Spend

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpendMockMvc.perform(put("/api/spends")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(spend)))
            .andExpect(status().isBadRequest());

        // Validate the Spend in the database
        List<Spend> spendList = spendRepository.findAll();
        assertThat(spendList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Spend in Elasticsearch
        verify(mockSpendSearchRepository, times(0)).save(spend);
    }

    @Test
    @Transactional
    public void deleteSpend() throws Exception {
        // Initialize the database
        spendService.save(spend);

        int databaseSizeBeforeDelete = spendRepository.findAll().size();

        // Delete the spend
        restSpendMockMvc.perform(delete("/api/spends/{id}", spend.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Spend> spendList = spendRepository.findAll();
        assertThat(spendList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Spend in Elasticsearch
        verify(mockSpendSearchRepository, times(1)).deleteById(spend.getId());
    }

    @Test
    @Transactional
    public void searchSpend() throws Exception {
        // Initialize the database
        spendService.save(spend);
        when(mockSpendSearchRepository.search(queryStringQuery("id:" + spend.getId())))
            .thenReturn(Collections.singletonList(spend));
        // Search the spend
        restSpendMockMvc.perform(get("/api/_search/spends?query=id:" + spend.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(spend.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateOfUpdate").value(hasItem(DEFAULT_DATE_OF_UPDATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.[*].spendId").value(hasItem(DEFAULT_SPEND_ID)))
            .andExpect(jsonPath("$.[*].expenditureType").value(hasItem(DEFAULT_EXPENDITURE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].expenditureSubType").value(hasItem(DEFAULT_EXPENDITURE_SUB_TYPE.toString())))
            .andExpect(jsonPath("$.[*].successor").value(hasItem(DEFAULT_SUCCESSOR.toString())));
    }
}
