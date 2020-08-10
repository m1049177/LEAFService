package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.TechnologyRecommendation;
import com.mindtree.leafservice3.repository.TechnologyRecommendationRepository;
import com.mindtree.leafservice3.repository.search.TechnologyRecommendationSearchRepository;
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

import com.mindtree.leafservice3.domain.enumeration.TechnologyType;
/**
 * Integration tests for the {@link TechnologyRecommendationResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class TechnologyRecommendationResourceIT {

    private static final String DEFAULT_TECHNOLOGY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_TECHNOLOGY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_OUTDATED_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_OUTDATED_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_STABLE_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_STABLE_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_LATEST_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_LATEST_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_NEW_FEATURES = "AAAAAAAAAA";
    private static final String UPDATED_NEW_FEATURES = "BBBBBBBBBB";

    private static final String DEFAULT_TECHNOLOGY_TYPE = "CLIENT";
    private static final String UPDATED_TECHNOLOGY_TYPE = "SERVER";

    @Autowired
    private TechnologyRecommendationRepository technologyRecommendationRepository;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.TechnologyRecommendationSearchRepositoryMockConfiguration
     */
    @Autowired
    private TechnologyRecommendationSearchRepository mockTechnologyRecommendationSearchRepository;

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

    private MockMvc restTechnologyRecommendationMockMvc;

    private TechnologyRecommendation technologyRecommendation;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TechnologyRecommendationResource technologyRecommendationResource = new TechnologyRecommendationResource(technologyRecommendationRepository, mockTechnologyRecommendationSearchRepository);
        this.restTechnologyRecommendationMockMvc = MockMvcBuilders.standaloneSetup(technologyRecommendationResource)
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
    public static TechnologyRecommendation createEntity(EntityManager em) {
        TechnologyRecommendation technologyRecommendation = new TechnologyRecommendation()
            .technologyName(DEFAULT_TECHNOLOGY_NAME)
            .outdatedVersion(DEFAULT_OUTDATED_VERSION)
            .stableVersion(DEFAULT_STABLE_VERSION)
            .latestVersion(DEFAULT_LATEST_VERSION)
            .newFeatures(DEFAULT_NEW_FEATURES)
            .technologyType(DEFAULT_TECHNOLOGY_TYPE);
        return technologyRecommendation;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TechnologyRecommendation createUpdatedEntity(EntityManager em) {
        TechnologyRecommendation technologyRecommendation = new TechnologyRecommendation()
            .technologyName(UPDATED_TECHNOLOGY_NAME)
            .outdatedVersion(UPDATED_OUTDATED_VERSION)
            .stableVersion(UPDATED_STABLE_VERSION)
            .latestVersion(UPDATED_LATEST_VERSION)
            .newFeatures(UPDATED_NEW_FEATURES)
            .technologyType(UPDATED_TECHNOLOGY_TYPE);
        return technologyRecommendation;
    }

    @BeforeEach
    public void initTest() {
        technologyRecommendation = createEntity(em);
    }

    @Test
    @Transactional
    public void createTechnologyRecommendation() throws Exception {
        int databaseSizeBeforeCreate = technologyRecommendationRepository.findAll().size();

        // Create the TechnologyRecommendation
        restTechnologyRecommendationMockMvc.perform(post("/api/technology-recommendations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(technologyRecommendation)))
            .andExpect(status().isCreated());

        // Validate the TechnologyRecommendation in the database
        List<TechnologyRecommendation> technologyRecommendationList = technologyRecommendationRepository.findAll();
        assertThat(technologyRecommendationList).hasSize(databaseSizeBeforeCreate + 1);
        TechnologyRecommendation testTechnologyRecommendation = technologyRecommendationList.get(technologyRecommendationList.size() - 1);
        assertThat(testTechnologyRecommendation.getTechnologyName()).isEqualTo(DEFAULT_TECHNOLOGY_NAME);
        assertThat(testTechnologyRecommendation.getOutdatedVersion()).isEqualTo(DEFAULT_OUTDATED_VERSION);
        assertThat(testTechnologyRecommendation.getStableVersion()).isEqualTo(DEFAULT_STABLE_VERSION);
        assertThat(testTechnologyRecommendation.getLatestVersion()).isEqualTo(DEFAULT_LATEST_VERSION);
        assertThat(testTechnologyRecommendation.getNewFeatures()).isEqualTo(DEFAULT_NEW_FEATURES);
        assertThat(testTechnologyRecommendation.getTechnologyType()).isEqualTo(DEFAULT_TECHNOLOGY_TYPE);

        // Validate the TechnologyRecommendation in Elasticsearch
        verify(mockTechnologyRecommendationSearchRepository, times(1)).save(testTechnologyRecommendation);
    }

    @Test
    @Transactional
    public void createTechnologyRecommendationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = technologyRecommendationRepository.findAll().size();

        // Create the TechnologyRecommendation with an existing ID
        technologyRecommendation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTechnologyRecommendationMockMvc.perform(post("/api/technology-recommendations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(technologyRecommendation)))
            .andExpect(status().isBadRequest());

        // Validate the TechnologyRecommendation in the database
        List<TechnologyRecommendation> technologyRecommendationList = technologyRecommendationRepository.findAll();
        assertThat(technologyRecommendationList).hasSize(databaseSizeBeforeCreate);

        // Validate the TechnologyRecommendation in Elasticsearch
        verify(mockTechnologyRecommendationSearchRepository, times(0)).save(technologyRecommendation);
    }


    @Test
    @Transactional
    public void checkTechnologyNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = technologyRecommendationRepository.findAll().size();
        // set the field null
        technologyRecommendation.setTechnologyName(null);

        // Create the TechnologyRecommendation, which fails.

        restTechnologyRecommendationMockMvc.perform(post("/api/technology-recommendations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(technologyRecommendation)))
            .andExpect(status().isBadRequest());

        List<TechnologyRecommendation> technologyRecommendationList = technologyRecommendationRepository.findAll();
        assertThat(technologyRecommendationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStableVersionIsRequired() throws Exception {
        int databaseSizeBeforeTest = technologyRecommendationRepository.findAll().size();
        // set the field null
        technologyRecommendation.setStableVersion(null);

        // Create the TechnologyRecommendation, which fails.

        restTechnologyRecommendationMockMvc.perform(post("/api/technology-recommendations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(technologyRecommendation)))
            .andExpect(status().isBadRequest());

        List<TechnologyRecommendation> technologyRecommendationList = technologyRecommendationRepository.findAll();
        assertThat(technologyRecommendationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTechnologyRecommendations() throws Exception {
        // Initialize the database
        technologyRecommendationRepository.saveAndFlush(technologyRecommendation);

        // Get all the technologyRecommendationList
        restTechnologyRecommendationMockMvc.perform(get("/api/technology-recommendations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(technologyRecommendation.getId().intValue())))
            .andExpect(jsonPath("$.[*].technologyName").value(hasItem(DEFAULT_TECHNOLOGY_NAME)))
            .andExpect(jsonPath("$.[*].outdatedVersion").value(hasItem(DEFAULT_OUTDATED_VERSION)))
            .andExpect(jsonPath("$.[*].stableVersion").value(hasItem(DEFAULT_STABLE_VERSION)))
            .andExpect(jsonPath("$.[*].latestVersion").value(hasItem(DEFAULT_LATEST_VERSION)))
            .andExpect(jsonPath("$.[*].newFeatures").value(hasItem(DEFAULT_NEW_FEATURES)))
            .andExpect(jsonPath("$.[*].technologyType").value(hasItem(DEFAULT_TECHNOLOGY_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getTechnologyRecommendation() throws Exception {
        // Initialize the database
        technologyRecommendationRepository.saveAndFlush(technologyRecommendation);

        // Get the technologyRecommendation
        restTechnologyRecommendationMockMvc.perform(get("/api/technology-recommendations/{id}", technologyRecommendation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(technologyRecommendation.getId().intValue()))
            .andExpect(jsonPath("$.technologyName").value(DEFAULT_TECHNOLOGY_NAME))
            .andExpect(jsonPath("$.outdatedVersion").value(DEFAULT_OUTDATED_VERSION))
            .andExpect(jsonPath("$.stableVersion").value(DEFAULT_STABLE_VERSION))
            .andExpect(jsonPath("$.latestVersion").value(DEFAULT_LATEST_VERSION))
            .andExpect(jsonPath("$.newFeatures").value(DEFAULT_NEW_FEATURES))
            .andExpect(jsonPath("$.technologyType").value(DEFAULT_TECHNOLOGY_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTechnologyRecommendation() throws Exception {
        // Get the technologyRecommendation
        restTechnologyRecommendationMockMvc.perform(get("/api/technology-recommendations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTechnologyRecommendation() throws Exception {
        // Initialize the database
        technologyRecommendationRepository.saveAndFlush(technologyRecommendation);

        int databaseSizeBeforeUpdate = technologyRecommendationRepository.findAll().size();

        // Update the technologyRecommendation
        TechnologyRecommendation updatedTechnologyRecommendation = technologyRecommendationRepository.findById(technologyRecommendation.getId()).get();
        // Disconnect from session so that the updates on updatedTechnologyRecommendation are not directly saved in db
        em.detach(updatedTechnologyRecommendation);
        updatedTechnologyRecommendation
            .technologyName(UPDATED_TECHNOLOGY_NAME)
            .outdatedVersion(UPDATED_OUTDATED_VERSION)
            .stableVersion(UPDATED_STABLE_VERSION)
            .latestVersion(UPDATED_LATEST_VERSION)
            .newFeatures(UPDATED_NEW_FEATURES)
            .technologyType(UPDATED_TECHNOLOGY_TYPE);

        restTechnologyRecommendationMockMvc.perform(put("/api/technology-recommendations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTechnologyRecommendation)))
            .andExpect(status().isOk());

        // Validate the TechnologyRecommendation in the database
        List<TechnologyRecommendation> technologyRecommendationList = technologyRecommendationRepository.findAll();
        assertThat(technologyRecommendationList).hasSize(databaseSizeBeforeUpdate);
        TechnologyRecommendation testTechnologyRecommendation = technologyRecommendationList.get(technologyRecommendationList.size() - 1);
        assertThat(testTechnologyRecommendation.getTechnologyName()).isEqualTo(UPDATED_TECHNOLOGY_NAME);
        assertThat(testTechnologyRecommendation.getOutdatedVersion()).isEqualTo(UPDATED_OUTDATED_VERSION);
        assertThat(testTechnologyRecommendation.getStableVersion()).isEqualTo(UPDATED_STABLE_VERSION);
        assertThat(testTechnologyRecommendation.getLatestVersion()).isEqualTo(UPDATED_LATEST_VERSION);
        assertThat(testTechnologyRecommendation.getNewFeatures()).isEqualTo(UPDATED_NEW_FEATURES);
        assertThat(testTechnologyRecommendation.getTechnologyType()).isEqualTo(UPDATED_TECHNOLOGY_TYPE);

        // Validate the TechnologyRecommendation in Elasticsearch
        verify(mockTechnologyRecommendationSearchRepository, times(1)).save(testTechnologyRecommendation);
    }

    @Test
    @Transactional
    public void updateNonExistingTechnologyRecommendation() throws Exception {
        int databaseSizeBeforeUpdate = technologyRecommendationRepository.findAll().size();

        // Create the TechnologyRecommendation

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTechnologyRecommendationMockMvc.perform(put("/api/technology-recommendations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(technologyRecommendation)))
            .andExpect(status().isBadRequest());

        // Validate the TechnologyRecommendation in the database
        List<TechnologyRecommendation> technologyRecommendationList = technologyRecommendationRepository.findAll();
        assertThat(technologyRecommendationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TechnologyRecommendation in Elasticsearch
        verify(mockTechnologyRecommendationSearchRepository, times(0)).save(technologyRecommendation);
    }

    @Test
    @Transactional
    public void deleteTechnologyRecommendation() throws Exception {
        // Initialize the database
        technologyRecommendationRepository.saveAndFlush(technologyRecommendation);

        int databaseSizeBeforeDelete = technologyRecommendationRepository.findAll().size();

        // Delete the technologyRecommendation
        restTechnologyRecommendationMockMvc.perform(delete("/api/technology-recommendations/{id}", technologyRecommendation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TechnologyRecommendation> technologyRecommendationList = technologyRecommendationRepository.findAll();
        assertThat(technologyRecommendationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TechnologyRecommendation in Elasticsearch
        verify(mockTechnologyRecommendationSearchRepository, times(1)).deleteById(technologyRecommendation.getId());
    }

    @Test
    @Transactional
    public void searchTechnologyRecommendation() throws Exception {
        // Initialize the database
        technologyRecommendationRepository.saveAndFlush(technologyRecommendation);
        when(mockTechnologyRecommendationSearchRepository.search(queryStringQuery("id:" + technologyRecommendation.getId())))
            .thenReturn(Collections.singletonList(technologyRecommendation));
        // Search the technologyRecommendation
        restTechnologyRecommendationMockMvc.perform(get("/api/_search/technology-recommendations?query=id:" + technologyRecommendation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(technologyRecommendation.getId().intValue())))
            .andExpect(jsonPath("$.[*].technologyName").value(hasItem(DEFAULT_TECHNOLOGY_NAME)))
            .andExpect(jsonPath("$.[*].outdatedVersion").value(hasItem(DEFAULT_OUTDATED_VERSION)))
            .andExpect(jsonPath("$.[*].stableVersion").value(hasItem(DEFAULT_STABLE_VERSION)))
            .andExpect(jsonPath("$.[*].latestVersion").value(hasItem(DEFAULT_LATEST_VERSION)))
            .andExpect(jsonPath("$.[*].newFeatures").value(hasItem(DEFAULT_NEW_FEATURES)))
            .andExpect(jsonPath("$.[*].technologyType").value(hasItem(DEFAULT_TECHNOLOGY_TYPE.toString())));
    }
}
