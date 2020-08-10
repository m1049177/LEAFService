package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Expenditure;
import com.mindtree.leafservice3.domain.Application;
import com.mindtree.leafservice3.repository.ExpenditureRepository;
import com.mindtree.leafservice3.repository.search.ExpenditureSearchRepository;
import com.mindtree.leafservice3.service.ExpenditureService;
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

/**
 * Integration tests for the {@Link ExpenditureResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class ExpenditureResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_EXPENDITURE_TYPE = "INFRA";
    private static final String UPDATED_EXPENDITURE_TYPE = "LICENSE";

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private ExpenditureService expenditureService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.ExpenditureSearchRepositoryMockConfiguration
     */
    @Autowired
    private ExpenditureSearchRepository mockExpenditureSearchRepository;

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

    private MockMvc restExpenditureMockMvc;

    private Expenditure expenditure;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ExpenditureResource expenditureResource = new ExpenditureResource(expenditureService);
        this.restExpenditureMockMvc = MockMvcBuilders.standaloneSetup(expenditureResource)
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
    public static Expenditure createEntity(EntityManager em) {
        Expenditure expenditure = new Expenditure()
            .description(DEFAULT_DESCRIPTION)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .expenditureType(DEFAULT_EXPENDITURE_TYPE);
        // Add required entity
        Application application;
        if (TestUtil.findAll(em, Application.class).isEmpty()) {
            application = ApplicationResourceIT.createEntity(em);
            em.persist(application);
            em.flush();
        } else {
            application = TestUtil.findAll(em, Application.class).get(0);
        }
        expenditure.setApplication(application);
        return expenditure;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Expenditure createUpdatedEntity(EntityManager em) {
        Expenditure expenditure = new Expenditure()
            .description(UPDATED_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .expenditureType(UPDATED_EXPENDITURE_TYPE);
        // Add required entity
        Application application;
        if (TestUtil.findAll(em, Application.class).isEmpty()) {
            application = ApplicationResourceIT.createUpdatedEntity(em);
            em.persist(application);
            em.flush();
        } else {
            application = TestUtil.findAll(em, Application.class).get(0);
        }
        expenditure.setApplication(application);
        return expenditure;
    }

    @BeforeEach
    public void initTest() {
        expenditure = createEntity(em);
    }

    @Test
    @Transactional
    public void createExpenditure() throws Exception {
        int databaseSizeBeforeCreate = expenditureRepository.findAll().size();

        // Create the Expenditure
        restExpenditureMockMvc.perform(post("/api/expenditures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(expenditure)))
            .andExpect(status().isCreated());

        // Validate the Expenditure in the database
        List<Expenditure> expenditureList = expenditureRepository.findAll();
        assertThat(expenditureList).hasSize(databaseSizeBeforeCreate + 1);
        Expenditure testExpenditure = expenditureList.get(expenditureList.size() - 1);
        assertThat(testExpenditure.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testExpenditure.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testExpenditure.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testExpenditure.getExpenditureType()).isEqualTo(DEFAULT_EXPENDITURE_TYPE);

        // Validate the Expenditure in Elasticsearch
        verify(mockExpenditureSearchRepository, times(1)).save(testExpenditure);
    }

    @Test
    @Transactional
    public void createExpenditureWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = expenditureRepository.findAll().size();

        // Create the Expenditure with an existing ID
        expenditure.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restExpenditureMockMvc.perform(post("/api/expenditures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(expenditure)))
            .andExpect(status().isBadRequest());

        // Validate the Expenditure in the database
        List<Expenditure> expenditureList = expenditureRepository.findAll();
        assertThat(expenditureList).hasSize(databaseSizeBeforeCreate);

        // Validate the Expenditure in Elasticsearch
        verify(mockExpenditureSearchRepository, times(0)).save(expenditure);
    }


    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenditureRepository.findAll().size();
        // set the field null
        expenditure.setDescription(null);

        // Create the Expenditure, which fails.

        restExpenditureMockMvc.perform(post("/api/expenditures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(expenditure)))
            .andExpect(status().isBadRequest());

        List<Expenditure> expenditureList = expenditureRepository.findAll();
        assertThat(expenditureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenditureRepository.findAll().size();
        // set the field null
        expenditure.setStartDate(null);

        // Create the Expenditure, which fails.

        restExpenditureMockMvc.perform(post("/api/expenditures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(expenditure)))
            .andExpect(status().isBadRequest());

        List<Expenditure> expenditureList = expenditureRepository.findAll();
        assertThat(expenditureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkExpenditureTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = expenditureRepository.findAll().size();
        // set the field null
        expenditure.setExpenditureType(null);

        // Create the Expenditure, which fails.

        restExpenditureMockMvc.perform(post("/api/expenditures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(expenditure)))
            .andExpect(status().isBadRequest());

        List<Expenditure> expenditureList = expenditureRepository.findAll();
        assertThat(expenditureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllExpenditures() throws Exception {
        // Initialize the database
        expenditureRepository.saveAndFlush(expenditure);

        // Get all the expenditureList
        restExpenditureMockMvc.perform(get("/api/expenditures?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(expenditure.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].expenditureType").value(hasItem(DEFAULT_EXPENDITURE_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getExpenditure() throws Exception {
        // Initialize the database
        expenditureRepository.saveAndFlush(expenditure);

        // Get the expenditure
        restExpenditureMockMvc.perform(get("/api/expenditures/{id}", expenditure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(expenditure.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.expenditureType").value(DEFAULT_EXPENDITURE_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingExpenditure() throws Exception {
        // Get the expenditure
        restExpenditureMockMvc.perform(get("/api/expenditures/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateExpenditure() throws Exception {
        // Initialize the database
        expenditureService.save(expenditure);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockExpenditureSearchRepository);

        int databaseSizeBeforeUpdate = expenditureRepository.findAll().size();

        // Update the expenditure
        Expenditure updatedExpenditure = expenditureRepository.findById(expenditure.getId()).get();
        // Disconnect from session so that the updates on updatedExpenditure are not directly saved in db
        em.detach(updatedExpenditure);
        updatedExpenditure
            .description(UPDATED_DESCRIPTION)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .expenditureType(UPDATED_EXPENDITURE_TYPE);

        restExpenditureMockMvc.perform(put("/api/expenditures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedExpenditure)))
            .andExpect(status().isOk());

        // Validate the Expenditure in the database
        List<Expenditure> expenditureList = expenditureRepository.findAll();
        assertThat(expenditureList).hasSize(databaseSizeBeforeUpdate);
        Expenditure testExpenditure = expenditureList.get(expenditureList.size() - 1);
        assertThat(testExpenditure.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testExpenditure.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testExpenditure.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testExpenditure.getExpenditureType()).isEqualTo(UPDATED_EXPENDITURE_TYPE);

        // Validate the Expenditure in Elasticsearch
        verify(mockExpenditureSearchRepository, times(1)).save(testExpenditure);
    }

    @Test
    @Transactional
    public void updateNonExistingExpenditure() throws Exception {
        int databaseSizeBeforeUpdate = expenditureRepository.findAll().size();

        // Create the Expenditure

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExpenditureMockMvc.perform(put("/api/expenditures")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(expenditure)))
            .andExpect(status().isBadRequest());

        // Validate the Expenditure in the database
        List<Expenditure> expenditureList = expenditureRepository.findAll();
        assertThat(expenditureList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Expenditure in Elasticsearch
        verify(mockExpenditureSearchRepository, times(0)).save(expenditure);
    }

    @Test
    @Transactional
    public void deleteExpenditure() throws Exception {
        // Initialize the database
        expenditureService.save(expenditure);

        int databaseSizeBeforeDelete = expenditureRepository.findAll().size();

        // Delete the expenditure
        restExpenditureMockMvc.perform(delete("/api/expenditures/{id}", expenditure.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Expenditure> expenditureList = expenditureRepository.findAll();
        assertThat(expenditureList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Expenditure in Elasticsearch
        verify(mockExpenditureSearchRepository, times(1)).deleteById(expenditure.getId());
    }

    @Test
    @Transactional
    public void searchExpenditure() throws Exception {
        // Initialize the database
        expenditureService.save(expenditure);
        when(mockExpenditureSearchRepository.search(queryStringQuery("id:" + expenditure.getId())))
            .thenReturn(Collections.singletonList(expenditure));
        // Search the expenditure
        restExpenditureMockMvc.perform(get("/api/_search/expenditures?query=id:" + expenditure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(expenditure.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].expenditureType").value(hasItem(DEFAULT_EXPENDITURE_TYPE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Expenditure.class);
        Expenditure expenditure1 = new Expenditure();
        expenditure1.setId(1L);
        Expenditure expenditure2 = new Expenditure();
        expenditure2.setId(expenditure1.getId());
        assertThat(expenditure1).isEqualTo(expenditure2);
        expenditure2.setId(2L);
        assertThat(expenditure1).isNotEqualTo(expenditure2);
        expenditure1.setId(null);
        assertThat(expenditure1).isNotEqualTo(expenditure2);
    }
}
