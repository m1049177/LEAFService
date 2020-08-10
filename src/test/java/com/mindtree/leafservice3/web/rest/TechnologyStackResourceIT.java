package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.TechnologyStack;
import com.mindtree.leafservice3.repository.TechnologyStackRepository;
import com.mindtree.leafservice3.repository.search.TechnologyStackSearchRepository;
import com.mindtree.leafservice3.service.TechnologyStackService;
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
 * Integration tests for the {@Link TechnologyStackResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class TechnologyStackResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "CLIENT";
    private static final String UPDATED_TYPE = "SERVER";

    @Autowired
    private TechnologyStackRepository technologyStackRepository;

    @Autowired
    private TechnologyStackService technologyStackService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.TechnologyStackSearchRepositoryMockConfiguration
     */
    @Autowired
    private TechnologyStackSearchRepository mockTechnologyStackSearchRepository;

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

    private MockMvc restTechnologyStackMockMvc;

    private TechnologyStack technologyStack;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TechnologyStackResource technologyStackResource = new TechnologyStackResource(technologyStackService);
        this.restTechnologyStackMockMvc = MockMvcBuilders.standaloneSetup(technologyStackResource)
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
    public static TechnologyStack createEntity(EntityManager em) {
        TechnologyStack technologyStack = new TechnologyStack()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE);
        return technologyStack;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TechnologyStack createUpdatedEntity(EntityManager em) {
        TechnologyStack technologyStack = new TechnologyStack()
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE);
        return technologyStack;
    }

    @BeforeEach
    public void initTest() {
        technologyStack = createEntity(em);
    }

    @Test
    @Transactional
    public void createTechnologyStack() throws Exception {
        int databaseSizeBeforeCreate = technologyStackRepository.findAll().size();

        // Create the TechnologyStack
        restTechnologyStackMockMvc.perform(post("/api/technology-stacks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(technologyStack)))
            .andExpect(status().isCreated());

        // Validate the TechnologyStack in the database
        List<TechnologyStack> technologyStackList = technologyStackRepository.findAll();
        assertThat(technologyStackList).hasSize(databaseSizeBeforeCreate + 1);
        TechnologyStack testTechnologyStack = technologyStackList.get(technologyStackList.size() - 1);
        assertThat(testTechnologyStack.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTechnologyStack.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the TechnologyStack in Elasticsearch
        verify(mockTechnologyStackSearchRepository, times(1)).save(testTechnologyStack);
    }

    @Test
    @Transactional
    public void createTechnologyStackWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = technologyStackRepository.findAll().size();

        // Create the TechnologyStack with an existing ID
        technologyStack.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTechnologyStackMockMvc.perform(post("/api/technology-stacks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(technologyStack)))
            .andExpect(status().isBadRequest());

        // Validate the TechnologyStack in the database
        List<TechnologyStack> technologyStackList = technologyStackRepository.findAll();
        assertThat(technologyStackList).hasSize(databaseSizeBeforeCreate);

        // Validate the TechnologyStack in Elasticsearch
        verify(mockTechnologyStackSearchRepository, times(0)).save(technologyStack);
    }


    @Test
    @Transactional
    public void getAllTechnologyStacks() throws Exception {
        // Initialize the database
        technologyStackRepository.saveAndFlush(technologyStack);

        // Get all the technologyStackList
        restTechnologyStackMockMvc.perform(get("/api/technology-stacks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(technologyStack.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getTechnologyStack() throws Exception {
        // Initialize the database
        technologyStackRepository.saveAndFlush(technologyStack);

        // Get the technologyStack
        restTechnologyStackMockMvc.perform(get("/api/technology-stacks/{id}", technologyStack.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(technologyStack.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTechnologyStack() throws Exception {
        // Get the technologyStack
        restTechnologyStackMockMvc.perform(get("/api/technology-stacks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTechnologyStack() throws Exception {
        // Initialize the database
        technologyStackService.save(technologyStack);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockTechnologyStackSearchRepository);

        int databaseSizeBeforeUpdate = technologyStackRepository.findAll().size();

        // Update the technologyStack
        TechnologyStack updatedTechnologyStack = technologyStackRepository.findById(technologyStack.getId()).get();
        // Disconnect from session so that the updates on updatedTechnologyStack are not directly saved in db
        em.detach(updatedTechnologyStack);
        updatedTechnologyStack
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE);

        restTechnologyStackMockMvc.perform(put("/api/technology-stacks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTechnologyStack)))
            .andExpect(status().isOk());

        // Validate the TechnologyStack in the database
        List<TechnologyStack> technologyStackList = technologyStackRepository.findAll();
        assertThat(technologyStackList).hasSize(databaseSizeBeforeUpdate);
        TechnologyStack testTechnologyStack = technologyStackList.get(technologyStackList.size() - 1);
        assertThat(testTechnologyStack.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTechnologyStack.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the TechnologyStack in Elasticsearch
        verify(mockTechnologyStackSearchRepository, times(1)).save(testTechnologyStack);
    }

    @Test
    @Transactional
    public void updateNonExistingTechnologyStack() throws Exception {
        int databaseSizeBeforeUpdate = technologyStackRepository.findAll().size();

        // Create the TechnologyStack

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTechnologyStackMockMvc.perform(put("/api/technology-stacks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(technologyStack)))
            .andExpect(status().isBadRequest());

        // Validate the TechnologyStack in the database
        List<TechnologyStack> technologyStackList = technologyStackRepository.findAll();
        assertThat(technologyStackList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TechnologyStack in Elasticsearch
        verify(mockTechnologyStackSearchRepository, times(0)).save(technologyStack);
    }

    @Test
    @Transactional
    public void deleteTechnologyStack() throws Exception {
        // Initialize the database
        technologyStackService.save(technologyStack);

        int databaseSizeBeforeDelete = technologyStackRepository.findAll().size();

        // Delete the technologyStack
        restTechnologyStackMockMvc.perform(delete("/api/technology-stacks/{id}", technologyStack.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TechnologyStack> technologyStackList = technologyStackRepository.findAll();
        assertThat(technologyStackList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TechnologyStack in Elasticsearch
        verify(mockTechnologyStackSearchRepository, times(1)).deleteById(technologyStack.getId());
    }

    @Test
    @Transactional
    public void searchTechnologyStack() throws Exception {
        // Initialize the database
        technologyStackService.save(technologyStack);
        when(mockTechnologyStackSearchRepository.search(queryStringQuery("id:" + technologyStack.getId())))
            .thenReturn(Collections.singletonList(technologyStack));
        // Search the technologyStack
        restTechnologyStackMockMvc.perform(get("/api/_search/technology-stacks?query=id:" + technologyStack.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(technologyStack.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TechnologyStack.class);
        TechnologyStack technologyStack1 = new TechnologyStack();
        technologyStack1.setId(1L);
        TechnologyStack technologyStack2 = new TechnologyStack();
        technologyStack2.setId(technologyStack1.getId());
        assertThat(technologyStack1).isEqualTo(technologyStack2);
        technologyStack2.setId(2L);
        assertThat(technologyStack1).isNotEqualTo(technologyStack2);
        technologyStack1.setId(null);
        assertThat(technologyStack1).isNotEqualTo(technologyStack2);
    }
}
