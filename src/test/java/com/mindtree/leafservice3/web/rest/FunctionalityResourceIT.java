package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Functionality;
import com.mindtree.leafservice3.repository.FunctionalityRepository;
import com.mindtree.leafservice3.repository.search.FunctionalitySearchRepository;
import com.mindtree.leafservice3.service.FunctionalityService;
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
 * Integration tests for the {@Link FunctionalityResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class FunctionalityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MAJOR_MODULES = "AAAAAAAAAA";
    private static final String UPDATED_MAJOR_MODULES = "BBBBBBBBBB";

    @Autowired
    private FunctionalityRepository functionalityRepository;

    @Autowired
    private FunctionalityService functionalityService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.FunctionalitySearchRepositoryMockConfiguration
     */
    @Autowired
    private FunctionalitySearchRepository mockFunctionalitySearchRepository;

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

    private MockMvc restFunctionalityMockMvc;

    private Functionality functionality;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FunctionalityResource functionalityResource = new FunctionalityResource(functionalityService);
        this.restFunctionalityMockMvc = MockMvcBuilders.standaloneSetup(functionalityResource)
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
    public static Functionality createEntity(EntityManager em) {
        Functionality functionality = new Functionality()
            .name(DEFAULT_NAME)
            .majorModules(DEFAULT_MAJOR_MODULES);
        return functionality;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Functionality createUpdatedEntity(EntityManager em) {
        Functionality functionality = new Functionality()
            .name(UPDATED_NAME)
            .majorModules(UPDATED_MAJOR_MODULES);
        return functionality;
    }

    @BeforeEach
    public void initTest() {
        functionality = createEntity(em);
    }

    @Test
    @Transactional
    public void createFunctionality() throws Exception {
        int databaseSizeBeforeCreate = functionalityRepository.findAll().size();

        // Create the Functionality
        restFunctionalityMockMvc.perform(post("/api/functionalities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(functionality)))
            .andExpect(status().isCreated());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeCreate + 1);
        Functionality testFunctionality = functionalityList.get(functionalityList.size() - 1);
        assertThat(testFunctionality.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFunctionality.getMajorModules()).isEqualTo(DEFAULT_MAJOR_MODULES);

        // Validate the Functionality in Elasticsearch
        verify(mockFunctionalitySearchRepository, times(1)).save(testFunctionality);
    }

    @Test
    @Transactional
    public void createFunctionalityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = functionalityRepository.findAll().size();

        // Create the Functionality with an existing ID
        functionality.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFunctionalityMockMvc.perform(post("/api/functionalities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(functionality)))
            .andExpect(status().isBadRequest());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeCreate);

        // Validate the Functionality in Elasticsearch
        verify(mockFunctionalitySearchRepository, times(0)).save(functionality);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = functionalityRepository.findAll().size();
        // set the field null
        functionality.setName(null);

        // Create the Functionality, which fails.

        restFunctionalityMockMvc.perform(post("/api/functionalities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(functionality)))
            .andExpect(status().isBadRequest());

        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMajorModulesIsRequired() throws Exception {
        int databaseSizeBeforeTest = functionalityRepository.findAll().size();
        // set the field null
        functionality.setMajorModules(null);

        // Create the Functionality, which fails.

        restFunctionalityMockMvc.perform(post("/api/functionalities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(functionality)))
            .andExpect(status().isBadRequest());

        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFunctionalities() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        // Get all the functionalityList
        restFunctionalityMockMvc.perform(get("/api/functionalities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(functionality.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].majorModules").value(hasItem(DEFAULT_MAJOR_MODULES.toString())));
    }
    
    @Test
    @Transactional
    public void getFunctionality() throws Exception {
        // Initialize the database
        functionalityRepository.saveAndFlush(functionality);

        // Get the functionality
        restFunctionalityMockMvc.perform(get("/api/functionalities/{id}", functionality.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(functionality.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.majorModules").value(DEFAULT_MAJOR_MODULES.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFunctionality() throws Exception {
        // Get the functionality
        restFunctionalityMockMvc.perform(get("/api/functionalities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFunctionality() throws Exception {
        // Initialize the database
        functionalityService.save(functionality);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockFunctionalitySearchRepository);

        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();

        // Update the functionality
        Functionality updatedFunctionality = functionalityRepository.findById(functionality.getId()).get();
        // Disconnect from session so that the updates on updatedFunctionality are not directly saved in db
        em.detach(updatedFunctionality);
        updatedFunctionality
            .name(UPDATED_NAME)
            .majorModules(UPDATED_MAJOR_MODULES);

        restFunctionalityMockMvc.perform(put("/api/functionalities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedFunctionality)))
            .andExpect(status().isOk());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);
        Functionality testFunctionality = functionalityList.get(functionalityList.size() - 1);
        assertThat(testFunctionality.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFunctionality.getMajorModules()).isEqualTo(UPDATED_MAJOR_MODULES);

        // Validate the Functionality in Elasticsearch
        verify(mockFunctionalitySearchRepository, times(1)).save(testFunctionality);
    }

    @Test
    @Transactional
    public void updateNonExistingFunctionality() throws Exception {
        int databaseSizeBeforeUpdate = functionalityRepository.findAll().size();

        // Create the Functionality

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFunctionalityMockMvc.perform(put("/api/functionalities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(functionality)))
            .andExpect(status().isBadRequest());

        // Validate the Functionality in the database
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Functionality in Elasticsearch
        verify(mockFunctionalitySearchRepository, times(0)).save(functionality);
    }

    @Test
    @Transactional
    public void deleteFunctionality() throws Exception {
        // Initialize the database
        functionalityService.save(functionality);

        int databaseSizeBeforeDelete = functionalityRepository.findAll().size();

        // Delete the functionality
        restFunctionalityMockMvc.perform(delete("/api/functionalities/{id}", functionality.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Functionality> functionalityList = functionalityRepository.findAll();
        assertThat(functionalityList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Functionality in Elasticsearch
        verify(mockFunctionalitySearchRepository, times(1)).deleteById(functionality.getId());
    }

    @Test
    @Transactional
    public void searchFunctionality() throws Exception {
        // Initialize the database
        functionalityService.save(functionality);
        when(mockFunctionalitySearchRepository.search(queryStringQuery("id:" + functionality.getId())))
            .thenReturn(Collections.singletonList(functionality));
        // Search the functionality
        restFunctionalityMockMvc.perform(get("/api/_search/functionalities?query=id:" + functionality.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(functionality.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].majorModules").value(hasItem(DEFAULT_MAJOR_MODULES)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Functionality.class);
        Functionality functionality1 = new Functionality();
        functionality1.setId(1L);
        Functionality functionality2 = new Functionality();
        functionality2.setId(functionality1.getId());
        assertThat(functionality1).isEqualTo(functionality2);
        functionality2.setId(2L);
        assertThat(functionality1).isNotEqualTo(functionality2);
        functionality1.setId(null);
        assertThat(functionality1).isNotEqualTo(functionality2);
    }
}
