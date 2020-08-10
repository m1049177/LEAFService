package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Change;
import com.mindtree.leafservice3.repository.ChangeRepository;
import com.mindtree.leafservice3.repository.search.ChangeSearchRepository;
import com.mindtree.leafservice3.service.ChangeService;
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
 * Integration tests for the {@Link ChangeResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class ChangeResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_OF_CHANGE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_CHANGE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private ChangeRepository changeRepository;

    @Autowired
    private ChangeService changeService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.ChangeSearchRepositoryMockConfiguration
     */
    @Autowired
    private ChangeSearchRepository mockChangeSearchRepository;

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

    private MockMvc restChangeMockMvc;

    private Change change;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ChangeResource changeResource = new ChangeResource(changeService);
        this.restChangeMockMvc = MockMvcBuilders.standaloneSetup(changeResource)
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
    public static Change createEntity(EntityManager em) {
        Change change = new Change()
            .description(DEFAULT_DESCRIPTION)
            .dateOfChange(DEFAULT_DATE_OF_CHANGE);
        return change;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Change createUpdatedEntity(EntityManager em) {
        Change change = new Change()
            .description(UPDATED_DESCRIPTION)
            .dateOfChange(UPDATED_DATE_OF_CHANGE);
        return change;
    }

    @BeforeEach
    public void initTest() {
        change = createEntity(em);
    }

    @Test
    @Transactional
    public void createChange() throws Exception {
        int databaseSizeBeforeCreate = changeRepository.findAll().size();

        // Create the Change
        restChangeMockMvc.perform(post("/api/changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(change)))
            .andExpect(status().isCreated());

        // Validate the Change in the database
        List<Change> changeList = changeRepository.findAll();
        assertThat(changeList).hasSize(databaseSizeBeforeCreate + 1);
        Change testChange = changeList.get(changeList.size() - 1);
        assertThat(testChange.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testChange.getDateOfChange()).isEqualTo(DEFAULT_DATE_OF_CHANGE);

        // Validate the Change in Elasticsearch
        verify(mockChangeSearchRepository, times(1)).save(testChange);
    }

    @Test
    @Transactional
    public void createChangeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = changeRepository.findAll().size();

        // Create the Change with an existing ID
        change.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restChangeMockMvc.perform(post("/api/changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(change)))
            .andExpect(status().isBadRequest());

        // Validate the Change in the database
        List<Change> changeList = changeRepository.findAll();
        assertThat(changeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Change in Elasticsearch
        verify(mockChangeSearchRepository, times(0)).save(change);
    }


    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = changeRepository.findAll().size();
        // set the field null
        change.setDescription(null);

        // Create the Change, which fails.

        restChangeMockMvc.perform(post("/api/changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(change)))
            .andExpect(status().isBadRequest());

        List<Change> changeList = changeRepository.findAll();
        assertThat(changeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateOfChangeIsRequired() throws Exception {
        int databaseSizeBeforeTest = changeRepository.findAll().size();
        // set the field null
        change.setDateOfChange(null);

        // Create the Change, which fails.

        restChangeMockMvc.perform(post("/api/changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(change)))
            .andExpect(status().isBadRequest());

        List<Change> changeList = changeRepository.findAll();
        assertThat(changeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllChanges() throws Exception {
        // Initialize the database
        changeRepository.saveAndFlush(change);

        // Get all the changeList
        restChangeMockMvc.perform(get("/api/changes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(change.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].dateOfChange").value(hasItem(DEFAULT_DATE_OF_CHANGE.toString())));
    }
    
    @Test
    @Transactional
    public void getChange() throws Exception {
        // Initialize the database
        changeRepository.saveAndFlush(change);

        // Get the change
        restChangeMockMvc.perform(get("/api/changes/{id}", change.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(change.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.dateOfChange").value(DEFAULT_DATE_OF_CHANGE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingChange() throws Exception {
        // Get the change
        restChangeMockMvc.perform(get("/api/changes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateChange() throws Exception {
        // Initialize the database
        changeService.save(change);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockChangeSearchRepository);

        int databaseSizeBeforeUpdate = changeRepository.findAll().size();

        // Update the change
        Change updatedChange = changeRepository.findById(change.getId()).get();
        // Disconnect from session so that the updates on updatedChange are not directly saved in db
        em.detach(updatedChange);
        updatedChange
            .description(UPDATED_DESCRIPTION)
            .dateOfChange(UPDATED_DATE_OF_CHANGE);

        restChangeMockMvc.perform(put("/api/changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedChange)))
            .andExpect(status().isOk());

        // Validate the Change in the database
        List<Change> changeList = changeRepository.findAll();
        assertThat(changeList).hasSize(databaseSizeBeforeUpdate);
        Change testChange = changeList.get(changeList.size() - 1);
        assertThat(testChange.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testChange.getDateOfChange()).isEqualTo(UPDATED_DATE_OF_CHANGE);

        // Validate the Change in Elasticsearch
        verify(mockChangeSearchRepository, times(1)).save(testChange);
    }

    @Test
    @Transactional
    public void updateNonExistingChange() throws Exception {
        int databaseSizeBeforeUpdate = changeRepository.findAll().size();

        // Create the Change

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChangeMockMvc.perform(put("/api/changes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(change)))
            .andExpect(status().isBadRequest());

        // Validate the Change in the database
        List<Change> changeList = changeRepository.findAll();
        assertThat(changeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Change in Elasticsearch
        verify(mockChangeSearchRepository, times(0)).save(change);
    }

    @Test
    @Transactional
    public void deleteChange() throws Exception {
        // Initialize the database
        changeService.save(change);

        int databaseSizeBeforeDelete = changeRepository.findAll().size();

        // Delete the change
        restChangeMockMvc.perform(delete("/api/changes/{id}", change.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Change> changeList = changeRepository.findAll();
        assertThat(changeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Change in Elasticsearch
        verify(mockChangeSearchRepository, times(1)).deleteById(change.getId());
    }

    @Test
    @Transactional
    public void searchChange() throws Exception {
        // Initialize the database
        changeService.save(change);
        when(mockChangeSearchRepository.search(queryStringQuery("id:" + change.getId())))
            .thenReturn(Collections.singletonList(change));
        // Search the change
        restChangeMockMvc.perform(get("/api/_search/changes?query=id:" + change.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(change.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dateOfChange").value(hasItem(DEFAULT_DATE_OF_CHANGE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Change.class);
        Change change1 = new Change();
        change1.setId(1L);
        Change change2 = new Change();
        change2.setId(change1.getId());
        assertThat(change1).isEqualTo(change2);
        change2.setId(2L);
        assertThat(change1).isNotEqualTo(change2);
        change1.setId(null);
        assertThat(change1).isNotEqualTo(change2);
    }
}
