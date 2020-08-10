package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Capabilities;
import com.mindtree.leafservice3.repository.CapabilitiesRepository;
import com.mindtree.leafservice3.repository.search.CapabilitiesSearchRepository;
import com.mindtree.leafservice3.service.CapabilitiesService;
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

/**
 * Integration tests for the {@Link CapabilitiesResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class CapabilitiesResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private CapabilitiesRepository capabilitiesRepository;

    @Autowired
    private CapabilitiesService capabilitiesService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.CapabilitiesSearchRepositoryMockConfiguration
     */
    @Autowired
    private CapabilitiesSearchRepository mockCapabilitiesSearchRepository;

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

    private MockMvc restCapabilitiesMockMvc;

    private Capabilities capabilities;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CapabilitiesResource capabilitiesResource = new CapabilitiesResource(capabilitiesService);
        this.restCapabilitiesMockMvc = MockMvcBuilders.standaloneSetup(capabilitiesResource)
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
    public static Capabilities createEntity(EntityManager em) {
        Capabilities capabilities = new Capabilities()
            .description(DEFAULT_DESCRIPTION);
        return capabilities;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Capabilities createUpdatedEntity(EntityManager em) {
        Capabilities capabilities = new Capabilities()
            .description(UPDATED_DESCRIPTION);
        return capabilities;
    }

    @BeforeEach
    public void initTest() {
        capabilities = createEntity(em);
    }

    @Test
    @Transactional
    public void createCapabilities() throws Exception {
        int databaseSizeBeforeCreate = capabilitiesRepository.findAll().size();

        // Create the Capabilities
        restCapabilitiesMockMvc.perform(post("/api/capabilities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capabilities)))
            .andExpect(status().isCreated());

        // Validate the Capabilities in the database
        List<Capabilities> capabilitiesList = capabilitiesRepository.findAll();
        assertThat(capabilitiesList).hasSize(databaseSizeBeforeCreate + 1);
        Capabilities testCapabilities = capabilitiesList.get(capabilitiesList.size() - 1);
        assertThat(testCapabilities.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Capabilities in Elasticsearch
        verify(mockCapabilitiesSearchRepository, times(1)).save(testCapabilities);
    }

    @Test
    @Transactional
    public void createCapabilitiesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = capabilitiesRepository.findAll().size();

        // Create the Capabilities with an existing ID
        capabilities.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCapabilitiesMockMvc.perform(post("/api/capabilities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capabilities)))
            .andExpect(status().isBadRequest());

        // Validate the Capabilities in the database
        List<Capabilities> capabilitiesList = capabilitiesRepository.findAll();
        assertThat(capabilitiesList).hasSize(databaseSizeBeforeCreate);

        // Validate the Capabilities in Elasticsearch
        verify(mockCapabilitiesSearchRepository, times(0)).save(capabilities);
    }


    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = capabilitiesRepository.findAll().size();
        // set the field null
        capabilities.setDescription(null);

        // Create the Capabilities, which fails.

        restCapabilitiesMockMvc.perform(post("/api/capabilities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capabilities)))
            .andExpect(status().isBadRequest());

        List<Capabilities> capabilitiesList = capabilitiesRepository.findAll();
        assertThat(capabilitiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCapabilities() throws Exception {
        // Initialize the database
        capabilitiesRepository.saveAndFlush(capabilities);

        // Get all the capabilitiesList
        restCapabilitiesMockMvc.perform(get("/api/capabilities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capabilities.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
    
    @Test
    @Transactional
    public void getCapabilities() throws Exception {
        // Initialize the database
        capabilitiesRepository.saveAndFlush(capabilities);

        // Get the capabilities
        restCapabilitiesMockMvc.perform(get("/api/capabilities/{id}", capabilities.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(capabilities.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCapabilities() throws Exception {
        // Get the capabilities
        restCapabilitiesMockMvc.perform(get("/api/capabilities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCapabilities() throws Exception {
        // Initialize the database
        capabilitiesService.save(capabilities);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockCapabilitiesSearchRepository);

        int databaseSizeBeforeUpdate = capabilitiesRepository.findAll().size();

        // Update the capabilities
        Capabilities updatedCapabilities = capabilitiesRepository.findById(capabilities.getId()).get();
        // Disconnect from session so that the updates on updatedCapabilities are not directly saved in db
        em.detach(updatedCapabilities);
        updatedCapabilities
            .description(UPDATED_DESCRIPTION);

        restCapabilitiesMockMvc.perform(put("/api/capabilities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCapabilities)))
            .andExpect(status().isOk());

        // Validate the Capabilities in the database
        List<Capabilities> capabilitiesList = capabilitiesRepository.findAll();
        assertThat(capabilitiesList).hasSize(databaseSizeBeforeUpdate);
        Capabilities testCapabilities = capabilitiesList.get(capabilitiesList.size() - 1);
        assertThat(testCapabilities.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Capabilities in Elasticsearch
        verify(mockCapabilitiesSearchRepository, times(1)).save(testCapabilities);
    }

    @Test
    @Transactional
    public void updateNonExistingCapabilities() throws Exception {
        int databaseSizeBeforeUpdate = capabilitiesRepository.findAll().size();

        // Create the Capabilities

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCapabilitiesMockMvc.perform(put("/api/capabilities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capabilities)))
            .andExpect(status().isBadRequest());

        // Validate the Capabilities in the database
        List<Capabilities> capabilitiesList = capabilitiesRepository.findAll();
        assertThat(capabilitiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Capabilities in Elasticsearch
        verify(mockCapabilitiesSearchRepository, times(0)).save(capabilities);
    }

    @Test
    @Transactional
    public void deleteCapabilities() throws Exception {
        // Initialize the database
        capabilitiesService.save(capabilities);

        int databaseSizeBeforeDelete = capabilitiesRepository.findAll().size();

        // Delete the capabilities
        restCapabilitiesMockMvc.perform(delete("/api/capabilities/{id}", capabilities.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Capabilities> capabilitiesList = capabilitiesRepository.findAll();
        assertThat(capabilitiesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Capabilities in Elasticsearch
        verify(mockCapabilitiesSearchRepository, times(1)).deleteById(capabilities.getId());
    }

    @Test
    @Transactional
    public void searchCapabilities() throws Exception {
        // Initialize the database
        capabilitiesService.save(capabilities);
        when(mockCapabilitiesSearchRepository.search(queryStringQuery("id:" + capabilities.getId())))
            .thenReturn(Collections.singletonList(capabilities));
        // Search the capabilities
        restCapabilitiesMockMvc.perform(get("/api/_search/capabilities?query=id:" + capabilities.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capabilities.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Capabilities.class);
        Capabilities capabilities1 = new Capabilities();
        capabilities1.setId(1L);
        Capabilities capabilities2 = new Capabilities();
        capabilities2.setId(capabilities1.getId());
        assertThat(capabilities1).isEqualTo(capabilities2);
        capabilities2.setId(2L);
        assertThat(capabilities1).isNotEqualTo(capabilities2);
        capabilities1.setId(null);
        assertThat(capabilities1).isNotEqualTo(capabilities2);
    }
}
