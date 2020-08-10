package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.BusinessProcess;
import com.mindtree.leafservice3.repository.BusinessProcessRepository;
import com.mindtree.leafservice3.repository.search.BusinessProcessSearchRepository;
import com.mindtree.leafservice3.service.BusinessProcessService;
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

import com.mindtree.leafservice3.domain.enumeration.ProcessStatus;
/**
 * Integration tests for the {@Link BusinessProcessResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class BusinessProcessResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_EXPECTED_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_EXPECTED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final ProcessStatus DEFAULT_STATUS = ProcessStatus.Completed;
    private static final ProcessStatus UPDATED_STATUS = ProcessStatus.InProgress;

    @Autowired
    private BusinessProcessRepository businessProcessRepository;

    @Autowired
    private BusinessProcessService businessProcessService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.BusinessProcessSearchRepositoryMockConfiguration
     */
    @Autowired
    private BusinessProcessSearchRepository mockBusinessProcessSearchRepository;

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

    private MockMvc restBusinessProcessMockMvc;

    private BusinessProcess businessProcess;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BusinessProcessResource businessProcessResource = new BusinessProcessResource(businessProcessService);
        this.restBusinessProcessMockMvc = MockMvcBuilders.standaloneSetup(businessProcessResource)
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
    public static BusinessProcess createEntity(EntityManager em) {
        BusinessProcess businessProcess = new BusinessProcess()
            .name(DEFAULT_NAME)
            .startDate(DEFAULT_START_DATE)
            .expectedEndDate(DEFAULT_EXPECTED_END_DATE)
            .endDate(DEFAULT_END_DATE)
            .status(DEFAULT_STATUS);
        return businessProcess;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessProcess createUpdatedEntity(EntityManager em) {
        BusinessProcess businessProcess = new BusinessProcess()
            .name(UPDATED_NAME)
            .startDate(UPDATED_START_DATE)
            .expectedEndDate(UPDATED_EXPECTED_END_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS);
        return businessProcess;
    }

    @BeforeEach
    public void initTest() {
        businessProcess = createEntity(em);
    }

    @Test
    @Transactional
    public void createBusinessProcess() throws Exception {
        int databaseSizeBeforeCreate = businessProcessRepository.findAll().size();

        // Create the BusinessProcess
        restBusinessProcessMockMvc.perform(post("/api/business-processes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessProcess)))
            .andExpect(status().isCreated());

        // Validate the BusinessProcess in the database
        List<BusinessProcess> businessProcessList = businessProcessRepository.findAll();
        assertThat(businessProcessList).hasSize(databaseSizeBeforeCreate + 1);
        BusinessProcess testBusinessProcess = businessProcessList.get(businessProcessList.size() - 1);
        assertThat(testBusinessProcess.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBusinessProcess.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testBusinessProcess.getExpectedEndDate()).isEqualTo(DEFAULT_EXPECTED_END_DATE);
        assertThat(testBusinessProcess.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testBusinessProcess.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the BusinessProcess in Elasticsearch
        verify(mockBusinessProcessSearchRepository, times(1)).save(testBusinessProcess);
    }

    @Test
    @Transactional
    public void createBusinessProcessWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = businessProcessRepository.findAll().size();

        // Create the BusinessProcess with an existing ID
        businessProcess.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBusinessProcessMockMvc.perform(post("/api/business-processes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessProcess)))
            .andExpect(status().isBadRequest());

        // Validate the BusinessProcess in the database
        List<BusinessProcess> businessProcessList = businessProcessRepository.findAll();
        assertThat(businessProcessList).hasSize(databaseSizeBeforeCreate);

        // Validate the BusinessProcess in Elasticsearch
        verify(mockBusinessProcessSearchRepository, times(0)).save(businessProcess);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessProcessRepository.findAll().size();
        // set the field null
        businessProcess.setName(null);

        // Create the BusinessProcess, which fails.

        restBusinessProcessMockMvc.perform(post("/api/business-processes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessProcess)))
            .andExpect(status().isBadRequest());

        List<BusinessProcess> businessProcessList = businessProcessRepository.findAll();
        assertThat(businessProcessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessProcessRepository.findAll().size();
        // set the field null
        businessProcess.setStartDate(null);

        // Create the BusinessProcess, which fails.

        restBusinessProcessMockMvc.perform(post("/api/business-processes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessProcess)))
            .andExpect(status().isBadRequest());

        List<BusinessProcess> businessProcessList = businessProcessRepository.findAll();
        assertThat(businessProcessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessProcessRepository.findAll().size();
        // set the field null
        businessProcess.setStatus(null);

        // Create the BusinessProcess, which fails.

        restBusinessProcessMockMvc.perform(post("/api/business-processes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessProcess)))
            .andExpect(status().isBadRequest());

        List<BusinessProcess> businessProcessList = businessProcessRepository.findAll();
        assertThat(businessProcessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBusinessProcesses() throws Exception {
        // Initialize the database
        businessProcessRepository.saveAndFlush(businessProcess);

        // Get all the businessProcessList
        restBusinessProcessMockMvc.perform(get("/api/business-processes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessProcess.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].expectedEndDate").value(hasItem(DEFAULT_EXPECTED_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    @Transactional
    public void getBusinessProcess() throws Exception {
        // Initialize the database
        businessProcessRepository.saveAndFlush(businessProcess);

        // Get the businessProcess
        restBusinessProcessMockMvc.perform(get("/api/business-processes/{id}", businessProcess.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(businessProcess.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.expectedEndDate").value(DEFAULT_EXPECTED_END_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBusinessProcess() throws Exception {
        // Get the businessProcess
        restBusinessProcessMockMvc.perform(get("/api/business-processes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBusinessProcess() throws Exception {
        // Initialize the database
        businessProcessService.save(businessProcess);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockBusinessProcessSearchRepository);

        int databaseSizeBeforeUpdate = businessProcessRepository.findAll().size();

        // Update the businessProcess
        BusinessProcess updatedBusinessProcess = businessProcessRepository.findById(businessProcess.getId()).get();
        // Disconnect from session so that the updates on updatedBusinessProcess are not directly saved in db
        em.detach(updatedBusinessProcess);
        updatedBusinessProcess
            .name(UPDATED_NAME)
            .startDate(UPDATED_START_DATE)
            .expectedEndDate(UPDATED_EXPECTED_END_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS);

        restBusinessProcessMockMvc.perform(put("/api/business-processes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBusinessProcess)))
            .andExpect(status().isOk());

        // Validate the BusinessProcess in the database
        List<BusinessProcess> businessProcessList = businessProcessRepository.findAll();
        assertThat(businessProcessList).hasSize(databaseSizeBeforeUpdate);
        BusinessProcess testBusinessProcess = businessProcessList.get(businessProcessList.size() - 1);
        assertThat(testBusinessProcess.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBusinessProcess.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testBusinessProcess.getExpectedEndDate()).isEqualTo(UPDATED_EXPECTED_END_DATE);
        assertThat(testBusinessProcess.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testBusinessProcess.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the BusinessProcess in Elasticsearch
        verify(mockBusinessProcessSearchRepository, times(1)).save(testBusinessProcess);
    }

    @Test
    @Transactional
    public void updateNonExistingBusinessProcess() throws Exception {
        int databaseSizeBeforeUpdate = businessProcessRepository.findAll().size();

        // Create the BusinessProcess

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessProcessMockMvc.perform(put("/api/business-processes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessProcess)))
            .andExpect(status().isBadRequest());

        // Validate the BusinessProcess in the database
        List<BusinessProcess> businessProcessList = businessProcessRepository.findAll();
        assertThat(businessProcessList).hasSize(databaseSizeBeforeUpdate);

        // Validate the BusinessProcess in Elasticsearch
        verify(mockBusinessProcessSearchRepository, times(0)).save(businessProcess);
    }

    @Test
    @Transactional
    public void deleteBusinessProcess() throws Exception {
        // Initialize the database
        businessProcessService.save(businessProcess);

        int databaseSizeBeforeDelete = businessProcessRepository.findAll().size();

        // Delete the businessProcess
        restBusinessProcessMockMvc.perform(delete("/api/business-processes/{id}", businessProcess.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BusinessProcess> businessProcessList = businessProcessRepository.findAll();
        assertThat(businessProcessList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the BusinessProcess in Elasticsearch
        verify(mockBusinessProcessSearchRepository, times(1)).deleteById(businessProcess.getId());
    }

    @Test
    @Transactional
    public void searchBusinessProcess() throws Exception {
        // Initialize the database
        businessProcessService.save(businessProcess);
        when(mockBusinessProcessSearchRepository.search(queryStringQuery("id:" + businessProcess.getId())))
            .thenReturn(Collections.singletonList(businessProcess));
        // Search the businessProcess
        restBusinessProcessMockMvc.perform(get("/api/_search/business-processes?query=id:" + businessProcess.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessProcess.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].expectedEndDate").value(hasItem(DEFAULT_EXPECTED_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessProcess.class);
        BusinessProcess businessProcess1 = new BusinessProcess();
        businessProcess1.setId(1L);
        BusinessProcess businessProcess2 = new BusinessProcess();
        businessProcess2.setId(businessProcess1.getId());
        assertThat(businessProcess1).isEqualTo(businessProcess2);
        businessProcess2.setId(2L);
        assertThat(businessProcess1).isNotEqualTo(businessProcess2);
        businessProcess1.setId(null);
        assertThat(businessProcess1).isNotEqualTo(businessProcess2);
    }
}
