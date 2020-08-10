package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.BusinessFunction;
import com.mindtree.leafservice3.repository.BusinessFunctionRepository;
import com.mindtree.leafservice3.repository.search.BusinessFunctionSearchRepository;
import com.mindtree.leafservice3.service.BusinessFunctionService;
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

import com.mindtree.leafservice3.domain.enumeration.BusinessFunctionType;
/**
 * Integration tests for the {@Link BusinessFunctionResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class BusinessFunctionResourceIT {

    private static final BusinessFunctionType DEFAULT_TYPE = BusinessFunctionType.Primary;
    private static final BusinessFunctionType UPDATED_TYPE = BusinessFunctionType.Support;

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private BusinessFunctionRepository businessFunctionRepository;

    @Autowired
    private BusinessFunctionService businessFunctionService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.BusinessFunctionSearchRepositoryMockConfiguration
     */
    @Autowired
    private BusinessFunctionSearchRepository mockBusinessFunctionSearchRepository;

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

    private MockMvc restBusinessFunctionMockMvc;

    private BusinessFunction businessFunction;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BusinessFunctionResource businessFunctionResource = new BusinessFunctionResource(businessFunctionService);
        this.restBusinessFunctionMockMvc = MockMvcBuilders.standaloneSetup(businessFunctionResource)
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
    public static BusinessFunction createEntity(EntityManager em) {
        BusinessFunction businessFunction = new BusinessFunction()
            .type(DEFAULT_TYPE)
            .name(DEFAULT_NAME);
        return businessFunction;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessFunction createUpdatedEntity(EntityManager em) {
        BusinessFunction businessFunction = new BusinessFunction()
            .type(UPDATED_TYPE)
            .name(UPDATED_NAME);
        return businessFunction;
    }

    @BeforeEach
    public void initTest() {
        businessFunction = createEntity(em);
    }

    @Test
    @Transactional
    public void createBusinessFunction() throws Exception {
        int databaseSizeBeforeCreate = businessFunctionRepository.findAll().size();

        // Create the BusinessFunction
        restBusinessFunctionMockMvc.perform(post("/api/business-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessFunction)))
            .andExpect(status().isCreated());

        // Validate the BusinessFunction in the database
        List<BusinessFunction> businessFunctionList = businessFunctionRepository.findAll();
        assertThat(businessFunctionList).hasSize(databaseSizeBeforeCreate + 1);
        BusinessFunction testBusinessFunction = businessFunctionList.get(businessFunctionList.size() - 1);
        assertThat(testBusinessFunction.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testBusinessFunction.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the BusinessFunction in Elasticsearch
        verify(mockBusinessFunctionSearchRepository, times(1)).save(testBusinessFunction);
    }

    @Test
    @Transactional
    public void createBusinessFunctionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = businessFunctionRepository.findAll().size();

        // Create the BusinessFunction with an existing ID
        businessFunction.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBusinessFunctionMockMvc.perform(post("/api/business-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessFunction)))
            .andExpect(status().isBadRequest());

        // Validate the BusinessFunction in the database
        List<BusinessFunction> businessFunctionList = businessFunctionRepository.findAll();
        assertThat(businessFunctionList).hasSize(databaseSizeBeforeCreate);

        // Validate the BusinessFunction in Elasticsearch
        verify(mockBusinessFunctionSearchRepository, times(0)).save(businessFunction);
    }


    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessFunctionRepository.findAll().size();
        // set the field null
        businessFunction.setType(null);

        // Create the BusinessFunction, which fails.

        restBusinessFunctionMockMvc.perform(post("/api/business-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessFunction)))
            .andExpect(status().isBadRequest());

        List<BusinessFunction> businessFunctionList = businessFunctionRepository.findAll();
        assertThat(businessFunctionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = businessFunctionRepository.findAll().size();
        // set the field null
        businessFunction.setName(null);

        // Create the BusinessFunction, which fails.

        restBusinessFunctionMockMvc.perform(post("/api/business-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessFunction)))
            .andExpect(status().isBadRequest());

        List<BusinessFunction> businessFunctionList = businessFunctionRepository.findAll();
        assertThat(businessFunctionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBusinessFunctions() throws Exception {
        // Initialize the database
        businessFunctionRepository.saveAndFlush(businessFunction);

        // Get all the businessFunctionList
        restBusinessFunctionMockMvc.perform(get("/api/business-functions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessFunction.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getBusinessFunction() throws Exception {
        // Initialize the database
        businessFunctionRepository.saveAndFlush(businessFunction);

        // Get the businessFunction
        restBusinessFunctionMockMvc.perform(get("/api/business-functions/{id}", businessFunction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(businessFunction.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBusinessFunction() throws Exception {
        // Get the businessFunction
        restBusinessFunctionMockMvc.perform(get("/api/business-functions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBusinessFunction() throws Exception {
        // Initialize the database
        businessFunctionService.save(businessFunction);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockBusinessFunctionSearchRepository);

        int databaseSizeBeforeUpdate = businessFunctionRepository.findAll().size();

        // Update the businessFunction
        BusinessFunction updatedBusinessFunction = businessFunctionRepository.findById(businessFunction.getId()).get();
        // Disconnect from session so that the updates on updatedBusinessFunction are not directly saved in db
        em.detach(updatedBusinessFunction);
        updatedBusinessFunction
            .type(UPDATED_TYPE)
            .name(UPDATED_NAME);

        restBusinessFunctionMockMvc.perform(put("/api/business-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBusinessFunction)))
            .andExpect(status().isOk());

        // Validate the BusinessFunction in the database
        List<BusinessFunction> businessFunctionList = businessFunctionRepository.findAll();
        assertThat(businessFunctionList).hasSize(databaseSizeBeforeUpdate);
        BusinessFunction testBusinessFunction = businessFunctionList.get(businessFunctionList.size() - 1);
        assertThat(testBusinessFunction.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testBusinessFunction.getName()).isEqualTo(UPDATED_NAME);

        // Validate the BusinessFunction in Elasticsearch
        verify(mockBusinessFunctionSearchRepository, times(1)).save(testBusinessFunction);
    }

    @Test
    @Transactional
    public void updateNonExistingBusinessFunction() throws Exception {
        int databaseSizeBeforeUpdate = businessFunctionRepository.findAll().size();

        // Create the BusinessFunction

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessFunctionMockMvc.perform(put("/api/business-functions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(businessFunction)))
            .andExpect(status().isBadRequest());

        // Validate the BusinessFunction in the database
        List<BusinessFunction> businessFunctionList = businessFunctionRepository.findAll();
        assertThat(businessFunctionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the BusinessFunction in Elasticsearch
        verify(mockBusinessFunctionSearchRepository, times(0)).save(businessFunction);
    }

    @Test
    @Transactional
    public void deleteBusinessFunction() throws Exception {
        // Initialize the database
        businessFunctionService.save(businessFunction);

        int databaseSizeBeforeDelete = businessFunctionRepository.findAll().size();

        // Delete the businessFunction
        restBusinessFunctionMockMvc.perform(delete("/api/business-functions/{id}", businessFunction.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BusinessFunction> businessFunctionList = businessFunctionRepository.findAll();
        assertThat(businessFunctionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the BusinessFunction in Elasticsearch
        verify(mockBusinessFunctionSearchRepository, times(1)).deleteById(businessFunction.getId());
    }

    @Test
    @Transactional
    public void searchBusinessFunction() throws Exception {
        // Initialize the database
        businessFunctionService.save(businessFunction);
        when(mockBusinessFunctionSearchRepository.search(queryStringQuery("id:" + businessFunction.getId())))
            .thenReturn(Collections.singletonList(businessFunction));
        // Search the businessFunction
        restBusinessFunctionMockMvc.perform(get("/api/_search/business-functions?query=id:" + businessFunction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessFunction.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessFunction.class);
        BusinessFunction businessFunction1 = new BusinessFunction();
        businessFunction1.setId(1L);
        BusinessFunction businessFunction2 = new BusinessFunction();
        businessFunction2.setId(businessFunction1.getId());
        assertThat(businessFunction1).isEqualTo(businessFunction2);
        businessFunction2.setId(2L);
        assertThat(businessFunction1).isNotEqualTo(businessFunction2);
        businessFunction1.setId(null);
        assertThat(businessFunction1).isNotEqualTo(businessFunction2);
    }
}
