package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Evaluation;
import com.mindtree.leafservice3.domain.Application;
import com.mindtree.leafservice3.repository.EvaluationRepository;
import com.mindtree.leafservice3.repository.search.EvaluationSearchRepository;
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
import org.springframework.util.Base64Utils;
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

import com.mindtree.leafservice3.domain.enumeration.AssessmentCategory;

/**
 * Integration tests for the {@Link EvaluationResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class EvaluationResourceIT {

    private static final AssessmentCategory DEFAULT_ASSESSMENT_CATEGORY = AssessmentCategory.ARCHITECTUREPRINCIPLE;
    private static final AssessmentCategory UPDATED_ASSESSMENT_CATEGORY = AssessmentCategory.DEPLOYMENT;

    private static final Integer DEFAULT_SCORE = 1;
    private static final Integer UPDATED_SCORE = 2;

    private static final String DEFAULT_ASSESSMENT_RESULT = "AAAAAAAAAA";
    private static final String UPDATED_ASSESSMENT_RESULT = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ATTEMPT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ATTEMPT_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private EvaluationRepository evaluationRepository;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.EvaluationSearchRepositoryMockConfiguration
     */
    @Autowired
    private EvaluationSearchRepository mockEvaluationSearchRepository;

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

    private MockMvc restEvaluationMockMvc;

    private Evaluation evaluation;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EvaluationResource evaluationResource = new EvaluationResource(evaluationRepository, mockEvaluationSearchRepository);
        this.restEvaluationMockMvc = MockMvcBuilders.standaloneSetup(evaluationResource)
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
    public static Evaluation createEntity(EntityManager em) {
        Evaluation evaluation = new Evaluation()
            .assessmentCategory(DEFAULT_ASSESSMENT_CATEGORY)
            .score(DEFAULT_SCORE)
            .assessmentResult(DEFAULT_ASSESSMENT_RESULT)
            .attemptDate(DEFAULT_ATTEMPT_DATE);
        // Add required entity
        Application application;
        if (TestUtil.findAll(em, Application.class).isEmpty()) {
            application = ApplicationResourceIT.createEntity(em);
            em.persist(application);
            em.flush();
        } else {
            application = TestUtil.findAll(em, Application.class).get(0);
        }
        evaluation.setApplication(application);
        return evaluation;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evaluation createUpdatedEntity(EntityManager em) {
        Evaluation evaluation = new Evaluation()
            .assessmentCategory(UPDATED_ASSESSMENT_CATEGORY)
            .score(UPDATED_SCORE)
            .assessmentResult(UPDATED_ASSESSMENT_RESULT)
            .attemptDate(UPDATED_ATTEMPT_DATE);
        // Add required entity
        Application application;
        if (TestUtil.findAll(em, Application.class).isEmpty()) {
            application = ApplicationResourceIT.createUpdatedEntity(em);
            em.persist(application);
            em.flush();
        } else {
            application = TestUtil.findAll(em, Application.class).get(0);
        }
        evaluation.setApplication(application);
        return evaluation;
    }

    @BeforeEach
    public void initTest() {
        evaluation = createEntity(em);
    }

    @Test
    @Transactional
    public void createEvaluation() throws Exception {
        int databaseSizeBeforeCreate = evaluationRepository.findAll().size();

        // Create the Evaluation
        restEvaluationMockMvc.perform(post("/api/evaluations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(evaluation)))
            .andExpect(status().isCreated());

        // Validate the Evaluation in the database
        List<Evaluation> evaluationList = evaluationRepository.findAll();
        assertThat(evaluationList).hasSize(databaseSizeBeforeCreate + 1);
        Evaluation testEvaluation = evaluationList.get(evaluationList.size() - 1);
        assertThat(testEvaluation.getAssessmentCategory()).isEqualTo(DEFAULT_ASSESSMENT_CATEGORY);
        assertThat(testEvaluation.getScore()).isEqualTo(DEFAULT_SCORE);
        assertThat(testEvaluation.getAssessmentResult()).isEqualTo(DEFAULT_ASSESSMENT_RESULT);
        assertThat(testEvaluation.getAttemptDate()).isEqualTo(DEFAULT_ATTEMPT_DATE);

        // Validate the Evaluation in Elasticsearch
        verify(mockEvaluationSearchRepository, times(1)).save(testEvaluation);
    }

    @Test
    @Transactional
    public void createEvaluationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = evaluationRepository.findAll().size();

        // Create the Evaluation with an existing ID
        evaluation.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEvaluationMockMvc.perform(post("/api/evaluations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(evaluation)))
            .andExpect(status().isBadRequest());

        // Validate the Evaluation in the database
        List<Evaluation> evaluationList = evaluationRepository.findAll();
        assertThat(evaluationList).hasSize(databaseSizeBeforeCreate);

        // Validate the Evaluation in Elasticsearch
        verify(mockEvaluationSearchRepository, times(0)).save(evaluation);
    }


    @Test
    @Transactional
    public void checkAssessmentCategoryIsRequired() throws Exception {
        int databaseSizeBeforeTest = evaluationRepository.findAll().size();
        // set the field null
        evaluation.setAssessmentCategory(null);

        // Create the Evaluation, which fails.

        restEvaluationMockMvc.perform(post("/api/evaluations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(evaluation)))
            .andExpect(status().isBadRequest());

        List<Evaluation> evaluationList = evaluationRepository.findAll();
        assertThat(evaluationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkScoreIsRequired() throws Exception {
        int databaseSizeBeforeTest = evaluationRepository.findAll().size();
        // set the field null
        evaluation.setScore(null);

        // Create the Evaluation, which fails.

        restEvaluationMockMvc.perform(post("/api/evaluations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(evaluation)))
            .andExpect(status().isBadRequest());

        List<Evaluation> evaluationList = evaluationRepository.findAll();
        assertThat(evaluationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAttemptDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = evaluationRepository.findAll().size();
        // set the field null
        evaluation.setAttemptDate(null);

        // Create the Evaluation, which fails.

        restEvaluationMockMvc.perform(post("/api/evaluations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(evaluation)))
            .andExpect(status().isBadRequest());

        List<Evaluation> evaluationList = evaluationRepository.findAll();
        assertThat(evaluationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEvaluations() throws Exception {
        // Initialize the database
        evaluationRepository.saveAndFlush(evaluation);

        // Get all the evaluationList
        restEvaluationMockMvc.perform(get("/api/evaluations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(evaluation.getId().intValue())))
            .andExpect(jsonPath("$.[*].assessmentCategory").value(hasItem(DEFAULT_ASSESSMENT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].assessmentResult").value(hasItem(DEFAULT_ASSESSMENT_RESULT.toString())))
            .andExpect(jsonPath("$.[*].attemptDate").value(hasItem(DEFAULT_ATTEMPT_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getEvaluation() throws Exception {
        // Initialize the database
        evaluationRepository.saveAndFlush(evaluation);

        // Get the evaluation
        restEvaluationMockMvc.perform(get("/api/evaluations/{id}", evaluation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(evaluation.getId().intValue()))
            .andExpect(jsonPath("$.assessmentCategory").value(DEFAULT_ASSESSMENT_CATEGORY.toString()))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE))
            .andExpect(jsonPath("$.assessmentResult").value(DEFAULT_ASSESSMENT_RESULT.toString()))
            .andExpect(jsonPath("$.attemptDate").value(DEFAULT_ATTEMPT_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEvaluation() throws Exception {
        // Get the evaluation
        restEvaluationMockMvc.perform(get("/api/evaluations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEvaluation() throws Exception {
        // Initialize the database
        evaluationRepository.saveAndFlush(evaluation);

        int databaseSizeBeforeUpdate = evaluationRepository.findAll().size();

        // Update the evaluation
        Evaluation updatedEvaluation = evaluationRepository.findById(evaluation.getId()).get();
        // Disconnect from session so that the updates on updatedEvaluation are not directly saved in db
        em.detach(updatedEvaluation);
        updatedEvaluation
            .assessmentCategory(UPDATED_ASSESSMENT_CATEGORY)
            .score(UPDATED_SCORE)
            .assessmentResult(UPDATED_ASSESSMENT_RESULT)
            .attemptDate(UPDATED_ATTEMPT_DATE);

        restEvaluationMockMvc.perform(put("/api/evaluations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedEvaluation)))
            .andExpect(status().isOk());

        // Validate the Evaluation in the database
        List<Evaluation> evaluationList = evaluationRepository.findAll();
        assertThat(evaluationList).hasSize(databaseSizeBeforeUpdate);
        Evaluation testEvaluation = evaluationList.get(evaluationList.size() - 1);
        assertThat(testEvaluation.getAssessmentCategory()).isEqualTo(UPDATED_ASSESSMENT_CATEGORY);
        assertThat(testEvaluation.getScore()).isEqualTo(UPDATED_SCORE);
        assertThat(testEvaluation.getAssessmentResult()).isEqualTo(UPDATED_ASSESSMENT_RESULT);
        assertThat(testEvaluation.getAttemptDate()).isEqualTo(UPDATED_ATTEMPT_DATE);

        // Validate the Evaluation in Elasticsearch
        verify(mockEvaluationSearchRepository, times(1)).save(testEvaluation);
    }

    @Test
    @Transactional
    public void updateNonExistingEvaluation() throws Exception {
        int databaseSizeBeforeUpdate = evaluationRepository.findAll().size();

        // Create the Evaluation

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEvaluationMockMvc.perform(put("/api/evaluations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(evaluation)))
            .andExpect(status().isBadRequest());

        // Validate the Evaluation in the database
        List<Evaluation> evaluationList = evaluationRepository.findAll();
        assertThat(evaluationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Evaluation in Elasticsearch
        verify(mockEvaluationSearchRepository, times(0)).save(evaluation);
    }

    @Test
    @Transactional
    public void deleteEvaluation() throws Exception {
        // Initialize the database
        evaluationRepository.saveAndFlush(evaluation);

        int databaseSizeBeforeDelete = evaluationRepository.findAll().size();

        // Delete the evaluation
        restEvaluationMockMvc.perform(delete("/api/evaluations/{id}", evaluation.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Evaluation> evaluationList = evaluationRepository.findAll();
        assertThat(evaluationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Evaluation in Elasticsearch
        verify(mockEvaluationSearchRepository, times(1)).deleteById(evaluation.getId());
    }

    @Test
    @Transactional
    public void searchEvaluation() throws Exception {
        // Initialize the database
        evaluationRepository.saveAndFlush(evaluation);
        when(mockEvaluationSearchRepository.search(queryStringQuery("id:" + evaluation.getId())))
            .thenReturn(Collections.singletonList(evaluation));
        // Search the evaluation
        restEvaluationMockMvc.perform(get("/api/_search/evaluations?query=id:" + evaluation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(evaluation.getId().intValue())))
            .andExpect(jsonPath("$.[*].assessmentCategory").value(hasItem(DEFAULT_ASSESSMENT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].assessmentResult").value(hasItem(DEFAULT_ASSESSMENT_RESULT.toString())))
            .andExpect(jsonPath("$.[*].attemptDate").value(hasItem(DEFAULT_ATTEMPT_DATE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Evaluation.class);
        Evaluation evaluation1 = new Evaluation();
        evaluation1.setId(1L);
        Evaluation evaluation2 = new Evaluation();
        evaluation2.setId(evaluation1.getId());
        assertThat(evaluation1).isEqualTo(evaluation2);
        evaluation2.setId(2L);
        assertThat(evaluation1).isNotEqualTo(evaluation2);
        evaluation1.setId(null);
        assertThat(evaluation1).isNotEqualTo(evaluation2);
    }
}
