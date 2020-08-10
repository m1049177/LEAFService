package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.LineOfBusiness;
import com.mindtree.leafservice3.repository.LineOfBusinessRepository;
import com.mindtree.leafservice3.repository.search.LineOfBusinessSearchRepository;
import com.mindtree.leafservice3.service.LineOfBusinessService;
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
 * Integration tests for the {@Link LineOfBusinessResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class LineOfBusinessResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private LineOfBusinessRepository lineOfBusinessRepository;

    @Autowired
    private LineOfBusinessService lineOfBusinessService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.LineOfBusinessSearchRepositoryMockConfiguration
     */
    @Autowired
    private LineOfBusinessSearchRepository mockLineOfBusinessSearchRepository;

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

    private MockMvc restLineOfBusinessMockMvc;

    private LineOfBusiness lineOfBusiness;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LineOfBusinessResource lineOfBusinessResource = new LineOfBusinessResource(lineOfBusinessService);
        this.restLineOfBusinessMockMvc = MockMvcBuilders.standaloneSetup(lineOfBusinessResource)
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
    public static LineOfBusiness createEntity(EntityManager em) {
        LineOfBusiness lineOfBusiness = new LineOfBusiness()
            .name(DEFAULT_NAME);
        return lineOfBusiness;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LineOfBusiness createUpdatedEntity(EntityManager em) {
        LineOfBusiness lineOfBusiness = new LineOfBusiness()
            .name(UPDATED_NAME);
        return lineOfBusiness;
    }

    @BeforeEach
    public void initTest() {
        lineOfBusiness = createEntity(em);
    }

    @Test
    @Transactional
    public void createLineOfBusiness() throws Exception {
        int databaseSizeBeforeCreate = lineOfBusinessRepository.findAll().size();

        // Create the LineOfBusiness
        restLineOfBusinessMockMvc.perform(post("/api/line-of-businesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lineOfBusiness)))
            .andExpect(status().isCreated());

        // Validate the LineOfBusiness in the database
        List<LineOfBusiness> lineOfBusinessList = lineOfBusinessRepository.findAll();
        assertThat(lineOfBusinessList).hasSize(databaseSizeBeforeCreate + 1);
        LineOfBusiness testLineOfBusiness = lineOfBusinessList.get(lineOfBusinessList.size() - 1);
        assertThat(testLineOfBusiness.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the LineOfBusiness in Elasticsearch
        verify(mockLineOfBusinessSearchRepository, times(1)).save(testLineOfBusiness);
    }

    @Test
    @Transactional
    public void createLineOfBusinessWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = lineOfBusinessRepository.findAll().size();

        // Create the LineOfBusiness with an existing ID
        lineOfBusiness.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLineOfBusinessMockMvc.perform(post("/api/line-of-businesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lineOfBusiness)))
            .andExpect(status().isBadRequest());

        // Validate the LineOfBusiness in the database
        List<LineOfBusiness> lineOfBusinessList = lineOfBusinessRepository.findAll();
        assertThat(lineOfBusinessList).hasSize(databaseSizeBeforeCreate);

        // Validate the LineOfBusiness in Elasticsearch
        verify(mockLineOfBusinessSearchRepository, times(0)).save(lineOfBusiness);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = lineOfBusinessRepository.findAll().size();
        // set the field null
        lineOfBusiness.setName(null);

        // Create the LineOfBusiness, which fails.

        restLineOfBusinessMockMvc.perform(post("/api/line-of-businesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lineOfBusiness)))
            .andExpect(status().isBadRequest());

        List<LineOfBusiness> lineOfBusinessList = lineOfBusinessRepository.findAll();
        assertThat(lineOfBusinessList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllLineOfBusinesses() throws Exception {
        // Initialize the database
        lineOfBusinessRepository.saveAndFlush(lineOfBusiness);

        // Get all the lineOfBusinessList
        restLineOfBusinessMockMvc.perform(get("/api/line-of-businesses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lineOfBusiness.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getLineOfBusiness() throws Exception {
        // Initialize the database
        lineOfBusinessRepository.saveAndFlush(lineOfBusiness);

        // Get the lineOfBusiness
        restLineOfBusinessMockMvc.perform(get("/api/line-of-businesses/{id}", lineOfBusiness.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(lineOfBusiness.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLineOfBusiness() throws Exception {
        // Get the lineOfBusiness
        restLineOfBusinessMockMvc.perform(get("/api/line-of-businesses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLineOfBusiness() throws Exception {
        // Initialize the database
        lineOfBusinessService.save(lineOfBusiness);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockLineOfBusinessSearchRepository);

        int databaseSizeBeforeUpdate = lineOfBusinessRepository.findAll().size();

        // Update the lineOfBusiness
        LineOfBusiness updatedLineOfBusiness = lineOfBusinessRepository.findById(lineOfBusiness.getId()).get();
        // Disconnect from session so that the updates on updatedLineOfBusiness are not directly saved in db
        em.detach(updatedLineOfBusiness);
        updatedLineOfBusiness
            .name(UPDATED_NAME);

        restLineOfBusinessMockMvc.perform(put("/api/line-of-businesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLineOfBusiness)))
            .andExpect(status().isOk());

        // Validate the LineOfBusiness in the database
        List<LineOfBusiness> lineOfBusinessList = lineOfBusinessRepository.findAll();
        assertThat(lineOfBusinessList).hasSize(databaseSizeBeforeUpdate);
        LineOfBusiness testLineOfBusiness = lineOfBusinessList.get(lineOfBusinessList.size() - 1);
        assertThat(testLineOfBusiness.getName()).isEqualTo(UPDATED_NAME);

        // Validate the LineOfBusiness in Elasticsearch
        verify(mockLineOfBusinessSearchRepository, times(1)).save(testLineOfBusiness);
    }

    @Test
    @Transactional
    public void updateNonExistingLineOfBusiness() throws Exception {
        int databaseSizeBeforeUpdate = lineOfBusinessRepository.findAll().size();

        // Create the LineOfBusiness

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLineOfBusinessMockMvc.perform(put("/api/line-of-businesses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(lineOfBusiness)))
            .andExpect(status().isBadRequest());

        // Validate the LineOfBusiness in the database
        List<LineOfBusiness> lineOfBusinessList = lineOfBusinessRepository.findAll();
        assertThat(lineOfBusinessList).hasSize(databaseSizeBeforeUpdate);

        // Validate the LineOfBusiness in Elasticsearch
        verify(mockLineOfBusinessSearchRepository, times(0)).save(lineOfBusiness);
    }

    @Test
    @Transactional
    public void deleteLineOfBusiness() throws Exception {
        // Initialize the database
        lineOfBusinessService.save(lineOfBusiness);

        int databaseSizeBeforeDelete = lineOfBusinessRepository.findAll().size();

        // Delete the lineOfBusiness
        restLineOfBusinessMockMvc.perform(delete("/api/line-of-businesses/{id}", lineOfBusiness.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<LineOfBusiness> lineOfBusinessList = lineOfBusinessRepository.findAll();
        assertThat(lineOfBusinessList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the LineOfBusiness in Elasticsearch
        verify(mockLineOfBusinessSearchRepository, times(1)).deleteById(lineOfBusiness.getId());
    }

    @Test
    @Transactional
    public void searchLineOfBusiness() throws Exception {
        // Initialize the database
        lineOfBusinessService.save(lineOfBusiness);
        when(mockLineOfBusinessSearchRepository.search(queryStringQuery("id:" + lineOfBusiness.getId())))
            .thenReturn(Collections.singletonList(lineOfBusiness));
        // Search the lineOfBusiness
        restLineOfBusinessMockMvc.perform(get("/api/_search/line-of-businesses?query=id:" + lineOfBusiness.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lineOfBusiness.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LineOfBusiness.class);
        LineOfBusiness lineOfBusiness1 = new LineOfBusiness();
        lineOfBusiness1.setId(1L);
        LineOfBusiness lineOfBusiness2 = new LineOfBusiness();
        lineOfBusiness2.setId(lineOfBusiness1.getId());
        assertThat(lineOfBusiness1).isEqualTo(lineOfBusiness2);
        lineOfBusiness2.setId(2L);
        assertThat(lineOfBusiness1).isNotEqualTo(lineOfBusiness2);
        lineOfBusiness1.setId(null);
        assertThat(lineOfBusiness1).isNotEqualTo(lineOfBusiness2);
    }
}
