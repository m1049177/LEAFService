package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Integration;
import com.mindtree.leafservice3.domain.Application;
import com.mindtree.leafservice3.repository.IntegrationRepository;
import com.mindtree.leafservice3.repository.search.IntegrationSearchRepository;
import com.mindtree.leafservice3.service.IntegrationService;
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
import java.util.Collections;
import java.util.List;

import static com.mindtree.leafservice3.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mindtree.leafservice3.domain.enumeration.IntegrationFlowType;
/**
 * Integration tests for the {@link IntegrationResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class IntegrationResourceIT {

    private static final IntegrationFlowType DEFAULT_FLOW_TYPE = IntegrationFlowType.INBOUND;
    private static final IntegrationFlowType UPDATED_FLOW_TYPE = IntegrationFlowType.OUTBOUND;

    private static final String DEFAULT_ENTITY = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY = "BBBBBBBBBB";

    @Autowired
    private IntegrationRepository integrationRepository;

    @Autowired
    private IntegrationService integrationService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.IntegrationSearchRepositoryMockConfiguration
     */
    @Autowired
    private IntegrationSearchRepository mockIntegrationSearchRepository;

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

    private MockMvc restIntegrationMockMvc;

    private Integration integration;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final IntegrationResource integrationResource = new IntegrationResource(integrationService);
        this.restIntegrationMockMvc = MockMvcBuilders.standaloneSetup(integrationResource)
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
    public static Integration createEntity(EntityManager em) {
        Integration integration = new Integration()
            .flowType(DEFAULT_FLOW_TYPE)
            .entity(DEFAULT_ENTITY);
        // Add required entity
        Application application;
        if (TestUtil.findAll(em, Application.class).isEmpty()) {
            application = ApplicationResourceIT.createEntity(em);
            em.persist(application);
            em.flush();
        } else {
            application = TestUtil.findAll(em, Application.class).get(0);
        }
        integration.setIntegrationApp(application);
        return integration;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Integration createUpdatedEntity(EntityManager em) {
        Integration integration = new Integration()
            .flowType(UPDATED_FLOW_TYPE)
            .entity(UPDATED_ENTITY);
        // Add required entity
        Application application;
        if (TestUtil.findAll(em, Application.class).isEmpty()) {
            application = ApplicationResourceIT.createUpdatedEntity(em);
            em.persist(application);
            em.flush();
        } else {
            application = TestUtil.findAll(em, Application.class).get(0);
        }
        integration.setIntegrationApp(application);
        return integration;
    }

    @BeforeEach
    public void initTest() {
        integration = createEntity(em);
    }

    @Test
    @Transactional
    public void createIntegration() throws Exception {
        int databaseSizeBeforeCreate = integrationRepository.findAll().size();

        // Create the Integration
        restIntegrationMockMvc.perform(post("/api/integrations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(integration)))
            .andExpect(status().isCreated());

        // Validate the Integration in the database
        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeCreate + 1);
        Integration testIntegration = integrationList.get(integrationList.size() - 1);
        assertThat(testIntegration.getFlowType()).isEqualTo(DEFAULT_FLOW_TYPE);
        assertThat(testIntegration.getEntity()).isEqualTo(DEFAULT_ENTITY);

        // Validate the Integration in Elasticsearch
        verify(mockIntegrationSearchRepository, times(1)).save(testIntegration);
    }

    @Test
    @Transactional
    public void createIntegrationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = integrationRepository.findAll().size();

        // Create the Integration with an existing ID
        integration.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restIntegrationMockMvc.perform(post("/api/integrations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(integration)))
            .andExpect(status().isBadRequest());

        // Validate the Integration in the database
        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeCreate);

        // Validate the Integration in Elasticsearch
        verify(mockIntegrationSearchRepository, times(0)).save(integration);
    }


    @Test
    @Transactional
    public void checkFlowTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = integrationRepository.findAll().size();
        // set the field null
        integration.setFlowType(null);

        // Create the Integration, which fails.

        restIntegrationMockMvc.perform(post("/api/integrations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(integration)))
            .andExpect(status().isBadRequest());

        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEntityIsRequired() throws Exception {
        int databaseSizeBeforeTest = integrationRepository.findAll().size();
        // set the field null
        integration.setEntity(null);

        // Create the Integration, which fails.

        restIntegrationMockMvc.perform(post("/api/integrations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(integration)))
            .andExpect(status().isBadRequest());

        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllIntegrations() throws Exception {
        // Initialize the database
        integrationRepository.saveAndFlush(integration);

        // Get all the integrationList
        restIntegrationMockMvc.perform(get("/api/integrations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(integration.getId().intValue())))
            .andExpect(jsonPath("$.[*].flowType").value(hasItem(DEFAULT_FLOW_TYPE.toString())))
            .andExpect(jsonPath("$.[*].entity").value(hasItem(DEFAULT_ENTITY)));
    }
    
    @Test
    @Transactional
    public void getIntegration() throws Exception {
        // Initialize the database
        integrationRepository.saveAndFlush(integration);

        // Get the integration
        restIntegrationMockMvc.perform(get("/api/integrations/{id}", integration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(integration.getId().intValue()))
            .andExpect(jsonPath("$.flowType").value(DEFAULT_FLOW_TYPE.toString()))
            .andExpect(jsonPath("$.entity").value(DEFAULT_ENTITY));
    }

    @Test
    @Transactional
    public void getNonExistingIntegration() throws Exception {
        // Get the integration
        restIntegrationMockMvc.perform(get("/api/integrations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIntegration() throws Exception {
        // Initialize the database
        integrationService.save(integration);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockIntegrationSearchRepository);

        int databaseSizeBeforeUpdate = integrationRepository.findAll().size();

        // Update the integration
        Integration updatedIntegration = integrationRepository.findById(integration.getId()).get();
        // Disconnect from session so that the updates on updatedIntegration are not directly saved in db
        em.detach(updatedIntegration);
        updatedIntegration
            .flowType(UPDATED_FLOW_TYPE)
            .entity(UPDATED_ENTITY);

        restIntegrationMockMvc.perform(put("/api/integrations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedIntegration)))
            .andExpect(status().isOk());

        // Validate the Integration in the database
        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeUpdate);
        Integration testIntegration = integrationList.get(integrationList.size() - 1);
        assertThat(testIntegration.getFlowType()).isEqualTo(UPDATED_FLOW_TYPE);
        assertThat(testIntegration.getEntity()).isEqualTo(UPDATED_ENTITY);

        // Validate the Integration in Elasticsearch
        verify(mockIntegrationSearchRepository, times(1)).save(testIntegration);
    }

    @Test
    @Transactional
    public void updateNonExistingIntegration() throws Exception {
        int databaseSizeBeforeUpdate = integrationRepository.findAll().size();

        // Create the Integration

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIntegrationMockMvc.perform(put("/api/integrations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(integration)))
            .andExpect(status().isBadRequest());

        // Validate the Integration in the database
        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Integration in Elasticsearch
        verify(mockIntegrationSearchRepository, times(0)).save(integration);
    }

    @Test
    @Transactional
    public void deleteIntegration() throws Exception {
        // Initialize the database
        integrationService.save(integration);

        int databaseSizeBeforeDelete = integrationRepository.findAll().size();

        // Delete the integration
        restIntegrationMockMvc.perform(delete("/api/integrations/{id}", integration.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Integration> integrationList = integrationRepository.findAll();
        assertThat(integrationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Integration in Elasticsearch
        verify(mockIntegrationSearchRepository, times(1)).deleteById(integration.getId());
    }

    @Test
    @Transactional
    public void searchIntegration() throws Exception {
        // Initialize the database
        integrationService.save(integration);
        when(mockIntegrationSearchRepository.search(queryStringQuery("id:" + integration.getId())))
            .thenReturn(Collections.singletonList(integration));
        // Search the integration
        restIntegrationMockMvc.perform(get("/api/_search/integrations?query=id:" + integration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(integration.getId().intValue())))
            .andExpect(jsonPath("$.[*].flowType").value(hasItem(DEFAULT_FLOW_TYPE.toString())))
            .andExpect(jsonPath("$.[*].entity").value(hasItem(DEFAULT_ENTITY)));
    }
}
