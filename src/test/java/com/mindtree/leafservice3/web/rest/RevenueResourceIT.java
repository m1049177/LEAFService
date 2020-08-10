package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Revenue;
import com.mindtree.leafservice3.repository.RevenueRepository;
import com.mindtree.leafservice3.repository.search.RevenueSearchRepository;
import com.mindtree.leafservice3.service.RevenueService;
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

import com.mindtree.leafservice3.domain.enumeration.CurrencySuccessor;
/**
 * Integration tests for the {@link RevenueResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class RevenueResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_AMOUNT = 1;
    private static final Integer UPDATED_AMOUNT = 2;

    private static final CurrencySuccessor DEFAULT_SUCCESSOR = CurrencySuccessor.NOSUCCESSOR;
    private static final CurrencySuccessor UPDATED_SUCCESSOR = CurrencySuccessor.K;

    @Autowired
    private RevenueRepository revenueRepository;

    @Autowired
    private RevenueService revenueService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.RevenueSearchRepositoryMockConfiguration
     */
    @Autowired
    private RevenueSearchRepository mockRevenueSearchRepository;

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

    private MockMvc restRevenueMockMvc;

    private Revenue revenue;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RevenueResource revenueResource = new RevenueResource(revenueService);
        this.restRevenueMockMvc = MockMvcBuilders.standaloneSetup(revenueResource)
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
    public static Revenue createEntity(EntityManager em) {
        Revenue revenue = new Revenue()
            .date(DEFAULT_DATE)
            .amount(DEFAULT_AMOUNT)
            .successor(DEFAULT_SUCCESSOR);
        return revenue;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Revenue createUpdatedEntity(EntityManager em) {
        Revenue revenue = new Revenue()
            .date(UPDATED_DATE)
            .amount(UPDATED_AMOUNT)
            .successor(UPDATED_SUCCESSOR);
        return revenue;
    }

    @BeforeEach
    public void initTest() {
        revenue = createEntity(em);
    }

    @Test
    @Transactional
    public void createRevenue() throws Exception {
        int databaseSizeBeforeCreate = revenueRepository.findAll().size();

        // Create the Revenue
        restRevenueMockMvc.perform(post("/api/revenues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(revenue)))
            .andExpect(status().isCreated());

        // Validate the Revenue in the database
        List<Revenue> revenueList = revenueRepository.findAll();
        assertThat(revenueList).hasSize(databaseSizeBeforeCreate + 1);
        Revenue testRevenue = revenueList.get(revenueList.size() - 1);
        assertThat(testRevenue.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testRevenue.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testRevenue.getSuccessor()).isEqualTo(DEFAULT_SUCCESSOR);

        // Validate the Revenue in Elasticsearch
        verify(mockRevenueSearchRepository, times(1)).save(testRevenue);
    }

    @Test
    @Transactional
    public void createRevenueWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = revenueRepository.findAll().size();

        // Create the Revenue with an existing ID
        revenue.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRevenueMockMvc.perform(post("/api/revenues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(revenue)))
            .andExpect(status().isBadRequest());

        // Validate the Revenue in the database
        List<Revenue> revenueList = revenueRepository.findAll();
        assertThat(revenueList).hasSize(databaseSizeBeforeCreate);

        // Validate the Revenue in Elasticsearch
        verify(mockRevenueSearchRepository, times(0)).save(revenue);
    }


    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = revenueRepository.findAll().size();
        // set the field null
        revenue.setDate(null);

        // Create the Revenue, which fails.

        restRevenueMockMvc.perform(post("/api/revenues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(revenue)))
            .andExpect(status().isBadRequest());

        List<Revenue> revenueList = revenueRepository.findAll();
        assertThat(revenueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSuccessorIsRequired() throws Exception {
        int databaseSizeBeforeTest = revenueRepository.findAll().size();
        // set the field null
        revenue.setSuccessor(null);

        // Create the Revenue, which fails.

        restRevenueMockMvc.perform(post("/api/revenues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(revenue)))
            .andExpect(status().isBadRequest());

        List<Revenue> revenueList = revenueRepository.findAll();
        assertThat(revenueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRevenues() throws Exception {
        // Initialize the database
        revenueRepository.saveAndFlush(revenue);

        // Get all the revenueList
        restRevenueMockMvc.perform(get("/api/revenues?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(revenue.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.[*].successor").value(hasItem(DEFAULT_SUCCESSOR.toString())));
    }
    
    @Test
    @Transactional
    public void getRevenue() throws Exception {
        // Initialize the database
        revenueRepository.saveAndFlush(revenue);

        // Get the revenue
        restRevenueMockMvc.perform(get("/api/revenues/{id}", revenue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(revenue.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT))
            .andExpect(jsonPath("$.successor").value(DEFAULT_SUCCESSOR.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRevenue() throws Exception {
        // Get the revenue
        restRevenueMockMvc.perform(get("/api/revenues/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRevenue() throws Exception {
        // Initialize the database
        revenueService.save(revenue);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockRevenueSearchRepository);

        int databaseSizeBeforeUpdate = revenueRepository.findAll().size();

        // Update the revenue
        Revenue updatedRevenue = revenueRepository.findById(revenue.getId()).get();
        // Disconnect from session so that the updates on updatedRevenue are not directly saved in db
        em.detach(updatedRevenue);
        updatedRevenue
            .date(UPDATED_DATE)
            .amount(UPDATED_AMOUNT)
            .successor(UPDATED_SUCCESSOR);

        restRevenueMockMvc.perform(put("/api/revenues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRevenue)))
            .andExpect(status().isOk());

        // Validate the Revenue in the database
        List<Revenue> revenueList = revenueRepository.findAll();
        assertThat(revenueList).hasSize(databaseSizeBeforeUpdate);
        Revenue testRevenue = revenueList.get(revenueList.size() - 1);
        assertThat(testRevenue.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testRevenue.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testRevenue.getSuccessor()).isEqualTo(UPDATED_SUCCESSOR);

        // Validate the Revenue in Elasticsearch
        verify(mockRevenueSearchRepository, times(1)).save(testRevenue);
    }

    @Test
    @Transactional
    public void updateNonExistingRevenue() throws Exception {
        int databaseSizeBeforeUpdate = revenueRepository.findAll().size();

        // Create the Revenue

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRevenueMockMvc.perform(put("/api/revenues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(revenue)))
            .andExpect(status().isBadRequest());

        // Validate the Revenue in the database
        List<Revenue> revenueList = revenueRepository.findAll();
        assertThat(revenueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Revenue in Elasticsearch
        verify(mockRevenueSearchRepository, times(0)).save(revenue);
    }

    @Test
    @Transactional
    public void deleteRevenue() throws Exception {
        // Initialize the database
        revenueService.save(revenue);

        int databaseSizeBeforeDelete = revenueRepository.findAll().size();

        // Delete the revenue
        restRevenueMockMvc.perform(delete("/api/revenues/{id}", revenue.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Revenue> revenueList = revenueRepository.findAll();
        assertThat(revenueList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Revenue in Elasticsearch
        verify(mockRevenueSearchRepository, times(1)).deleteById(revenue.getId());
    }

    @Test
    @Transactional
    public void searchRevenue() throws Exception {
        // Initialize the database
        revenueService.save(revenue);
        when(mockRevenueSearchRepository.search(queryStringQuery("id:" + revenue.getId())))
            .thenReturn(Collections.singletonList(revenue));
        // Search the revenue
        restRevenueMockMvc.perform(get("/api/_search/revenues?query=id:" + revenue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(revenue.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.[*].successor").value(hasItem(DEFAULT_SUCCESSOR.toString())));
    }
}
