package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.TechnologySuggestions;
import com.mindtree.leafservice3.repository.TechnologySuggestionsRepository;
import com.mindtree.leafservice3.repository.search.TechnologySuggestionsSearchRepository;
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

import com.mindtree.leafservice3.domain.enumeration.Type;
/**
 * Integration tests for the {@link TechnologySuggestionsResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class TechnologySuggestionsResourceIT {

    private static final Type DEFAULT_TYPE = Type.RISK;
    private static final Type UPDATED_TYPE = Type.WARNING;

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_SUGGESTION = "AAAAAAAAAA";
    private static final String UPDATED_SUGGESTION = "BBBBBBBBBB";

    @Autowired
    private TechnologySuggestionsRepository technologySuggestionsRepository;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.TechnologySuggestionsSearchRepositoryMockConfiguration
     */
    @Autowired
    private TechnologySuggestionsSearchRepository mockTechnologySuggestionsSearchRepository;

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

    private MockMvc restTechnologySuggestionsMockMvc;

    private TechnologySuggestions technologySuggestions;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TechnologySuggestionsResource technologySuggestionsResource = new TechnologySuggestionsResource(technologySuggestionsRepository, mockTechnologySuggestionsSearchRepository);
        this.restTechnologySuggestionsMockMvc = MockMvcBuilders.standaloneSetup(technologySuggestionsResource)
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
    public static TechnologySuggestions createEntity(EntityManager em) {
        TechnologySuggestions technologySuggestions = new TechnologySuggestions()
            .type(DEFAULT_TYPE)
            .version(DEFAULT_VERSION)
            .description(DEFAULT_DESCRIPTION)
            .suggestion(DEFAULT_SUGGESTION);
        return technologySuggestions;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TechnologySuggestions createUpdatedEntity(EntityManager em) {
        TechnologySuggestions technologySuggestions = new TechnologySuggestions()
            .type(UPDATED_TYPE)
            .version(UPDATED_VERSION)
            .description(UPDATED_DESCRIPTION)
            .suggestion(UPDATED_SUGGESTION);
        return technologySuggestions;
    }

    @BeforeEach
    public void initTest() {
        technologySuggestions = createEntity(em);
    }

    @Test
    @Transactional
    public void createTechnologySuggestions() throws Exception {
        int databaseSizeBeforeCreate = technologySuggestionsRepository.findAll().size();

        // Create the TechnologySuggestions
        restTechnologySuggestionsMockMvc.perform(post("/api/technology-suggestions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(technologySuggestions)))
            .andExpect(status().isCreated());

        // Validate the TechnologySuggestions in the database
        List<TechnologySuggestions> technologySuggestionsList = technologySuggestionsRepository.findAll();
        assertThat(technologySuggestionsList).hasSize(databaseSizeBeforeCreate + 1);
        TechnologySuggestions testTechnologySuggestions = technologySuggestionsList.get(technologySuggestionsList.size() - 1);
        assertThat(testTechnologySuggestions.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTechnologySuggestions.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testTechnologySuggestions.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTechnologySuggestions.getSuggestion()).isEqualTo(DEFAULT_SUGGESTION);

        // Validate the TechnologySuggestions in Elasticsearch
        verify(mockTechnologySuggestionsSearchRepository, times(1)).save(testTechnologySuggestions);
    }

    @Test
    @Transactional
    public void createTechnologySuggestionsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = technologySuggestionsRepository.findAll().size();

        // Create the TechnologySuggestions with an existing ID
        technologySuggestions.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTechnologySuggestionsMockMvc.perform(post("/api/technology-suggestions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(technologySuggestions)))
            .andExpect(status().isBadRequest());

        // Validate the TechnologySuggestions in the database
        List<TechnologySuggestions> technologySuggestionsList = technologySuggestionsRepository.findAll();
        assertThat(technologySuggestionsList).hasSize(databaseSizeBeforeCreate);

        // Validate the TechnologySuggestions in Elasticsearch
        verify(mockTechnologySuggestionsSearchRepository, times(0)).save(technologySuggestions);
    }


    @Test
    @Transactional
    public void getAllTechnologySuggestions() throws Exception {
        // Initialize the database
        technologySuggestionsRepository.saveAndFlush(technologySuggestions);

        // Get all the technologySuggestionsList
        restTechnologySuggestionsMockMvc.perform(get("/api/technology-suggestions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(technologySuggestions.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].suggestion").value(hasItem(DEFAULT_SUGGESTION)));
    }
    
    @Test
    @Transactional
    public void getTechnologySuggestions() throws Exception {
        // Initialize the database
        technologySuggestionsRepository.saveAndFlush(technologySuggestions);

        // Get the technologySuggestions
        restTechnologySuggestionsMockMvc.perform(get("/api/technology-suggestions/{id}", technologySuggestions.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(technologySuggestions.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.suggestion").value(DEFAULT_SUGGESTION));
    }

    @Test
    @Transactional
    public void getNonExistingTechnologySuggestions() throws Exception {
        // Get the technologySuggestions
        restTechnologySuggestionsMockMvc.perform(get("/api/technology-suggestions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTechnologySuggestions() throws Exception {
        // Initialize the database
        technologySuggestionsRepository.saveAndFlush(technologySuggestions);

        int databaseSizeBeforeUpdate = technologySuggestionsRepository.findAll().size();

        // Update the technologySuggestions
        TechnologySuggestions updatedTechnologySuggestions = technologySuggestionsRepository.findById(technologySuggestions.getId()).get();
        // Disconnect from session so that the updates on updatedTechnologySuggestions are not directly saved in db
        em.detach(updatedTechnologySuggestions);
        updatedTechnologySuggestions
            .type(UPDATED_TYPE)
            .version(UPDATED_VERSION)
            .description(UPDATED_DESCRIPTION)
            .suggestion(UPDATED_SUGGESTION);

        restTechnologySuggestionsMockMvc.perform(put("/api/technology-suggestions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedTechnologySuggestions)))
            .andExpect(status().isOk());

        // Validate the TechnologySuggestions in the database
        List<TechnologySuggestions> technologySuggestionsList = technologySuggestionsRepository.findAll();
        assertThat(technologySuggestionsList).hasSize(databaseSizeBeforeUpdate);
        TechnologySuggestions testTechnologySuggestions = technologySuggestionsList.get(technologySuggestionsList.size() - 1);
        assertThat(testTechnologySuggestions.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTechnologySuggestions.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testTechnologySuggestions.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTechnologySuggestions.getSuggestion()).isEqualTo(UPDATED_SUGGESTION);

        // Validate the TechnologySuggestions in Elasticsearch
        verify(mockTechnologySuggestionsSearchRepository, times(1)).save(testTechnologySuggestions);
    }

    @Test
    @Transactional
    public void updateNonExistingTechnologySuggestions() throws Exception {
        int databaseSizeBeforeUpdate = technologySuggestionsRepository.findAll().size();

        // Create the TechnologySuggestions

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTechnologySuggestionsMockMvc.perform(put("/api/technology-suggestions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(technologySuggestions)))
            .andExpect(status().isBadRequest());

        // Validate the TechnologySuggestions in the database
        List<TechnologySuggestions> technologySuggestionsList = technologySuggestionsRepository.findAll();
        assertThat(technologySuggestionsList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TechnologySuggestions in Elasticsearch
        verify(mockTechnologySuggestionsSearchRepository, times(0)).save(technologySuggestions);
    }

    @Test
    @Transactional
    public void deleteTechnologySuggestions() throws Exception {
        // Initialize the database
        technologySuggestionsRepository.saveAndFlush(technologySuggestions);

        int databaseSizeBeforeDelete = technologySuggestionsRepository.findAll().size();

        // Delete the technologySuggestions
        restTechnologySuggestionsMockMvc.perform(delete("/api/technology-suggestions/{id}", technologySuggestions.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TechnologySuggestions> technologySuggestionsList = technologySuggestionsRepository.findAll();
        assertThat(technologySuggestionsList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TechnologySuggestions in Elasticsearch
        verify(mockTechnologySuggestionsSearchRepository, times(1)).deleteById(technologySuggestions.getId());
    }

    @Test
    @Transactional
    public void searchTechnologySuggestions() throws Exception {
        // Initialize the database
        technologySuggestionsRepository.saveAndFlush(technologySuggestions);
        when(mockTechnologySuggestionsSearchRepository.search(queryStringQuery("id:" + technologySuggestions.getId())))
            .thenReturn(Collections.singletonList(technologySuggestions));
        // Search the technologySuggestions
        restTechnologySuggestionsMockMvc.perform(get("/api/_search/technology-suggestions?query=id:" + technologySuggestions.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(technologySuggestions.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].suggestion").value(hasItem(DEFAULT_SUGGESTION)));
    }
}
