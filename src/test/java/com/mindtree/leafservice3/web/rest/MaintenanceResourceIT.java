package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Maintenance;
import com.mindtree.leafservice3.repository.MaintenanceRepository;
import com.mindtree.leafservice3.repository.search.MaintenanceSearchRepository;
import com.mindtree.leafservice3.service.MaintenanceService;
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
 * Integration tests for the {@Link MaintenanceResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class MaintenanceResourceIT {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private MaintenanceService maintenanceService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.MaintenanceSearchRepositoryMockConfiguration
     */
    @Autowired
    private MaintenanceSearchRepository mockMaintenanceSearchRepository;

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

    private MockMvc restMaintenanceMockMvc;

    private Maintenance maintenance;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MaintenanceResource maintenanceResource = new MaintenanceResource(maintenanceService);
        this.restMaintenanceMockMvc = MockMvcBuilders.standaloneSetup(maintenanceResource)
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
    public static Maintenance createEntity(EntityManager em) {
        Maintenance maintenance = new Maintenance()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE);
        return maintenance;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Maintenance createUpdatedEntity(EntityManager em) {
        Maintenance maintenance = new Maintenance()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);
        return maintenance;
    }

    @BeforeEach
    public void initTest() {
        maintenance = createEntity(em);
    }

    @Test
    @Transactional
    public void createMaintenance() throws Exception {
        int databaseSizeBeforeCreate = maintenanceRepository.findAll().size();

        // Create the Maintenance
        restMaintenanceMockMvc.perform(post("/api/maintenances")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(maintenance)))
            .andExpect(status().isCreated());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeCreate + 1);
        Maintenance testMaintenance = maintenanceList.get(maintenanceList.size() - 1);
        assertThat(testMaintenance.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testMaintenance.getEndDate()).isEqualTo(DEFAULT_END_DATE);

        // Validate the Maintenance in Elasticsearch
        verify(mockMaintenanceSearchRepository, times(1)).save(testMaintenance);
    }

    @Test
    @Transactional
    public void createMaintenanceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = maintenanceRepository.findAll().size();

        // Create the Maintenance with an existing ID
        maintenance.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMaintenanceMockMvc.perform(post("/api/maintenances")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(maintenance)))
            .andExpect(status().isBadRequest());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeCreate);

        // Validate the Maintenance in Elasticsearch
        verify(mockMaintenanceSearchRepository, times(0)).save(maintenance);
    }


    @Test
    @Transactional
    public void checkStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = maintenanceRepository.findAll().size();
        // set the field null
        maintenance.setStartDate(null);

        // Create the Maintenance, which fails.

        restMaintenanceMockMvc.perform(post("/api/maintenances")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(maintenance)))
            .andExpect(status().isBadRequest());

        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = maintenanceRepository.findAll().size();
        // set the field null
        maintenance.setEndDate(null);

        // Create the Maintenance, which fails.

        restMaintenanceMockMvc.perform(post("/api/maintenances")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(maintenance)))
            .andExpect(status().isBadRequest());

        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMaintenances() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        // Get all the maintenanceList
        restMaintenanceMockMvc.perform(get("/api/maintenances?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(maintenance.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getMaintenance() throws Exception {
        // Initialize the database
        maintenanceRepository.saveAndFlush(maintenance);

        // Get the maintenance
        restMaintenanceMockMvc.perform(get("/api/maintenances/{id}", maintenance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(maintenance.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMaintenance() throws Exception {
        // Get the maintenance
        restMaintenanceMockMvc.perform(get("/api/maintenances/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMaintenance() throws Exception {
        // Initialize the database
        maintenanceService.save(maintenance);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockMaintenanceSearchRepository);

        int databaseSizeBeforeUpdate = maintenanceRepository.findAll().size();

        // Update the maintenance
        Maintenance updatedMaintenance = maintenanceRepository.findById(maintenance.getId()).get();
        // Disconnect from session so that the updates on updatedMaintenance are not directly saved in db
        em.detach(updatedMaintenance);
        updatedMaintenance
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE);

        restMaintenanceMockMvc.perform(put("/api/maintenances")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMaintenance)))
            .andExpect(status().isOk());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeUpdate);
        Maintenance testMaintenance = maintenanceList.get(maintenanceList.size() - 1);
        assertThat(testMaintenance.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testMaintenance.getEndDate()).isEqualTo(UPDATED_END_DATE);

        // Validate the Maintenance in Elasticsearch
        verify(mockMaintenanceSearchRepository, times(1)).save(testMaintenance);
    }

    @Test
    @Transactional
    public void updateNonExistingMaintenance() throws Exception {
        int databaseSizeBeforeUpdate = maintenanceRepository.findAll().size();

        // Create the Maintenance

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMaintenanceMockMvc.perform(put("/api/maintenances")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(maintenance)))
            .andExpect(status().isBadRequest());

        // Validate the Maintenance in the database
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Maintenance in Elasticsearch
        verify(mockMaintenanceSearchRepository, times(0)).save(maintenance);
    }

    @Test
    @Transactional
    public void deleteMaintenance() throws Exception {
        // Initialize the database
        maintenanceService.save(maintenance);

        int databaseSizeBeforeDelete = maintenanceRepository.findAll().size();

        // Delete the maintenance
        restMaintenanceMockMvc.perform(delete("/api/maintenances/{id}", maintenance.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Maintenance> maintenanceList = maintenanceRepository.findAll();
        assertThat(maintenanceList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Maintenance in Elasticsearch
        verify(mockMaintenanceSearchRepository, times(1)).deleteById(maintenance.getId());
    }

    @Test
    @Transactional
    public void searchMaintenance() throws Exception {
        // Initialize the database
        maintenanceService.save(maintenance);
        when(mockMaintenanceSearchRepository.search(queryStringQuery("id:" + maintenance.getId())))
            .thenReturn(Collections.singletonList(maintenance));
        // Search the maintenance
        restMaintenanceMockMvc.perform(get("/api/_search/maintenances?query=id:" + maintenance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(maintenance.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Maintenance.class);
        Maintenance maintenance1 = new Maintenance();
        maintenance1.setId(1L);
        Maintenance maintenance2 = new Maintenance();
        maintenance2.setId(maintenance1.getId());
        assertThat(maintenance1).isEqualTo(maintenance2);
        maintenance2.setId(2L);
        assertThat(maintenance1).isNotEqualTo(maintenance2);
        maintenance1.setId(null);
        assertThat(maintenance1).isNotEqualTo(maintenance2);
    }
}
