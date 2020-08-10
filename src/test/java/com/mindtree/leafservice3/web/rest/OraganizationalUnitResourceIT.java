package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.OraganizationalUnit;
import com.mindtree.leafservice3.repository.OraganizationalUnitRepository;
import com.mindtree.leafservice3.repository.search.OraganizationalUnitSearchRepository;
import com.mindtree.leafservice3.service.OraganizationalUnitService;
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
 * Integration tests for the {@Link OraganizationalUnitResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class OraganizationalUnitResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private OraganizationalUnitRepository oraganizationalUnitRepository;

    @Autowired
    private OraganizationalUnitService oraganizationalUnitService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.OraganizationalUnitSearchRepositoryMockConfiguration
     */
    @Autowired
    private OraganizationalUnitSearchRepository mockOraganizationalUnitSearchRepository;

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

    private MockMvc restOraganizationalUnitMockMvc;

    private OraganizationalUnit oraganizationalUnit;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OraganizationalUnitResource oraganizationalUnitResource = new OraganizationalUnitResource(oraganizationalUnitService);
        this.restOraganizationalUnitMockMvc = MockMvcBuilders.standaloneSetup(oraganizationalUnitResource)
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
    public static OraganizationalUnit createEntity(EntityManager em) {
        OraganizationalUnit oraganizationalUnit = new OraganizationalUnit()
            .name(DEFAULT_NAME);
        return oraganizationalUnit;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OraganizationalUnit createUpdatedEntity(EntityManager em) {
        OraganizationalUnit oraganizationalUnit = new OraganizationalUnit()
            .name(UPDATED_NAME);
        return oraganizationalUnit;
    }

    @BeforeEach
    public void initTest() {
        oraganizationalUnit = createEntity(em);
    }

    @Test
    @Transactional
    public void createOraganizationalUnit() throws Exception {
        int databaseSizeBeforeCreate = oraganizationalUnitRepository.findAll().size();

        // Create the OraganizationalUnit
        restOraganizationalUnitMockMvc.perform(post("/api/oraganizational-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(oraganizationalUnit)))
            .andExpect(status().isCreated());

        // Validate the OraganizationalUnit in the database
        List<OraganizationalUnit> oraganizationalUnitList = oraganizationalUnitRepository.findAll();
        assertThat(oraganizationalUnitList).hasSize(databaseSizeBeforeCreate + 1);
        OraganizationalUnit testOraganizationalUnit = oraganizationalUnitList.get(oraganizationalUnitList.size() - 1);
        assertThat(testOraganizationalUnit.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the OraganizationalUnit in Elasticsearch
        verify(mockOraganizationalUnitSearchRepository, times(1)).save(testOraganizationalUnit);
    }

    @Test
    @Transactional
    public void createOraganizationalUnitWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = oraganizationalUnitRepository.findAll().size();

        // Create the OraganizationalUnit with an existing ID
        oraganizationalUnit.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOraganizationalUnitMockMvc.perform(post("/api/oraganizational-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(oraganizationalUnit)))
            .andExpect(status().isBadRequest());

        // Validate the OraganizationalUnit in the database
        List<OraganizationalUnit> oraganizationalUnitList = oraganizationalUnitRepository.findAll();
        assertThat(oraganizationalUnitList).hasSize(databaseSizeBeforeCreate);

        // Validate the OraganizationalUnit in Elasticsearch
        verify(mockOraganizationalUnitSearchRepository, times(0)).save(oraganizationalUnit);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = oraganizationalUnitRepository.findAll().size();
        // set the field null
        oraganizationalUnit.setName(null);

        // Create the OraganizationalUnit, which fails.

        restOraganizationalUnitMockMvc.perform(post("/api/oraganizational-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(oraganizationalUnit)))
            .andExpect(status().isBadRequest());

        List<OraganizationalUnit> oraganizationalUnitList = oraganizationalUnitRepository.findAll();
        assertThat(oraganizationalUnitList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOraganizationalUnits() throws Exception {
        // Initialize the database
        oraganizationalUnitRepository.saveAndFlush(oraganizationalUnit);

        // Get all the oraganizationalUnitList
        restOraganizationalUnitMockMvc.perform(get("/api/oraganizational-units?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(oraganizationalUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getOraganizationalUnit() throws Exception {
        // Initialize the database
        oraganizationalUnitRepository.saveAndFlush(oraganizationalUnit);

        // Get the oraganizationalUnit
        restOraganizationalUnitMockMvc.perform(get("/api/oraganizational-units/{id}", oraganizationalUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(oraganizationalUnit.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOraganizationalUnit() throws Exception {
        // Get the oraganizationalUnit
        restOraganizationalUnitMockMvc.perform(get("/api/oraganizational-units/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOraganizationalUnit() throws Exception {
        // Initialize the database
        oraganizationalUnitService.save(oraganizationalUnit);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockOraganizationalUnitSearchRepository);

        int databaseSizeBeforeUpdate = oraganizationalUnitRepository.findAll().size();

        // Update the oraganizationalUnit
        OraganizationalUnit updatedOraganizationalUnit = oraganizationalUnitRepository.findById(oraganizationalUnit.getId()).get();
        // Disconnect from session so that the updates on updatedOraganizationalUnit are not directly saved in db
        em.detach(updatedOraganizationalUnit);
        updatedOraganizationalUnit
            .name(UPDATED_NAME);

        restOraganizationalUnitMockMvc.perform(put("/api/oraganizational-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOraganizationalUnit)))
            .andExpect(status().isOk());

        // Validate the OraganizationalUnit in the database
        List<OraganizationalUnit> oraganizationalUnitList = oraganizationalUnitRepository.findAll();
        assertThat(oraganizationalUnitList).hasSize(databaseSizeBeforeUpdate);
        OraganizationalUnit testOraganizationalUnit = oraganizationalUnitList.get(oraganizationalUnitList.size() - 1);
        assertThat(testOraganizationalUnit.getName()).isEqualTo(UPDATED_NAME);

        // Validate the OraganizationalUnit in Elasticsearch
        verify(mockOraganizationalUnitSearchRepository, times(1)).save(testOraganizationalUnit);
    }

    @Test
    @Transactional
    public void updateNonExistingOraganizationalUnit() throws Exception {
        int databaseSizeBeforeUpdate = oraganizationalUnitRepository.findAll().size();

        // Create the OraganizationalUnit

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOraganizationalUnitMockMvc.perform(put("/api/oraganizational-units")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(oraganizationalUnit)))
            .andExpect(status().isBadRequest());

        // Validate the OraganizationalUnit in the database
        List<OraganizationalUnit> oraganizationalUnitList = oraganizationalUnitRepository.findAll();
        assertThat(oraganizationalUnitList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OraganizationalUnit in Elasticsearch
        verify(mockOraganizationalUnitSearchRepository, times(0)).save(oraganizationalUnit);
    }

    @Test
    @Transactional
    public void deleteOraganizationalUnit() throws Exception {
        // Initialize the database
        oraganizationalUnitService.save(oraganizationalUnit);

        int databaseSizeBeforeDelete = oraganizationalUnitRepository.findAll().size();

        // Delete the oraganizationalUnit
        restOraganizationalUnitMockMvc.perform(delete("/api/oraganizational-units/{id}", oraganizationalUnit.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OraganizationalUnit> oraganizationalUnitList = oraganizationalUnitRepository.findAll();
        assertThat(oraganizationalUnitList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the OraganizationalUnit in Elasticsearch
        verify(mockOraganizationalUnitSearchRepository, times(1)).deleteById(oraganizationalUnit.getId());
    }

    @Test
    @Transactional
    public void searchOraganizationalUnit() throws Exception {
        // Initialize the database
        oraganizationalUnitService.save(oraganizationalUnit);
        when(mockOraganizationalUnitSearchRepository.search(queryStringQuery("id:" + oraganizationalUnit.getId())))
            .thenReturn(Collections.singletonList(oraganizationalUnit));
        // Search the oraganizationalUnit
        restOraganizationalUnitMockMvc.perform(get("/api/_search/oraganizational-units?query=id:" + oraganizationalUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(oraganizationalUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OraganizationalUnit.class);
        OraganizationalUnit oraganizationalUnit1 = new OraganizationalUnit();
        oraganizationalUnit1.setId(1L);
        OraganizationalUnit oraganizationalUnit2 = new OraganizationalUnit();
        oraganizationalUnit2.setId(oraganizationalUnit1.getId());
        assertThat(oraganizationalUnit1).isEqualTo(oraganizationalUnit2);
        oraganizationalUnit2.setId(2L);
        assertThat(oraganizationalUnit1).isNotEqualTo(oraganizationalUnit2);
        oraganizationalUnit1.setId(null);
        assertThat(oraganizationalUnit1).isNotEqualTo(oraganizationalUnit2);
    }
}
