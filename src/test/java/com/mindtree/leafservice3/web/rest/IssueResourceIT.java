package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Issue;
import com.mindtree.leafservice3.repository.IssueRepository;
import com.mindtree.leafservice3.repository.search.IssueSearchRepository;
import com.mindtree.leafservice3.service.IssueService;
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

import com.mindtree.leafservice3.domain.enumeration.IssueStatus;
import com.mindtree.leafservice3.domain.enumeration.TypeOfIssue;
/**
 * Integration tests for the {@Link IssueResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class IssueResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_OF_ISSUE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_ISSUE = LocalDate.now(ZoneId.systemDefault());

    private static final IssueStatus DEFAULT_STATUS = IssueStatus.Solved;
    private static final IssueStatus UPDATED_STATUS = IssueStatus.Open;

    private static final LocalDate DEFAULT_SOLVED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SOLVED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_SOLVED_BY = "AAAAAAAAAA";
    private static final String UPDATED_SOLVED_BY = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMBER_OF_DAYS = 1;
    private static final Integer UPDATED_NUMBER_OF_DAYS = 2;

    private static final TypeOfIssue DEFAULT_TYPE_OF_ISSUE = TypeOfIssue.Critical;
    private static final TypeOfIssue UPDATED_TYPE_OF_ISSUE = TypeOfIssue.Normal;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private IssueService issueService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.IssueSearchRepositoryMockConfiguration
     */
    @Autowired
    private IssueSearchRepository mockIssueSearchRepository;

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

    private MockMvc restIssueMockMvc;

    private Issue issue;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final IssueResource issueResource = new IssueResource(issueService);
        this.restIssueMockMvc = MockMvcBuilders.standaloneSetup(issueResource)
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
    public static Issue createEntity(EntityManager em) {
        Issue issue = new Issue()
            .description(DEFAULT_DESCRIPTION)
            .dateOfIssue(DEFAULT_DATE_OF_ISSUE)
            .status(DEFAULT_STATUS)
            .solvedDate(DEFAULT_SOLVED_DATE)
            .solvedBy(DEFAULT_SOLVED_BY)
            .numberOfDays(DEFAULT_NUMBER_OF_DAYS)
            .typeOfIssue(DEFAULT_TYPE_OF_ISSUE);
        return issue;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Issue createUpdatedEntity(EntityManager em) {
        Issue issue = new Issue()
            .description(UPDATED_DESCRIPTION)
            .dateOfIssue(UPDATED_DATE_OF_ISSUE)
            .status(UPDATED_STATUS)
            .solvedDate(UPDATED_SOLVED_DATE)
            .solvedBy(UPDATED_SOLVED_BY)
            .numberOfDays(UPDATED_NUMBER_OF_DAYS)
            .typeOfIssue(UPDATED_TYPE_OF_ISSUE);
        return issue;
    }

    @BeforeEach
    public void initTest() {
        issue = createEntity(em);
    }

    @Test
    @Transactional
    public void createIssue() throws Exception {
        int databaseSizeBeforeCreate = issueRepository.findAll().size();

        // Create the Issue
        restIssueMockMvc.perform(post("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(issue)))
            .andExpect(status().isCreated());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeCreate + 1);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testIssue.getDateOfIssue()).isEqualTo(DEFAULT_DATE_OF_ISSUE);
        assertThat(testIssue.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testIssue.getSolvedDate()).isEqualTo(DEFAULT_SOLVED_DATE);
        assertThat(testIssue.getSolvedBy()).isEqualTo(DEFAULT_SOLVED_BY);
        assertThat(testIssue.getNumberOfDays()).isEqualTo(DEFAULT_NUMBER_OF_DAYS);
        assertThat(testIssue.getTypeOfIssue()).isEqualTo(DEFAULT_TYPE_OF_ISSUE);

        // Validate the Issue in Elasticsearch
        verify(mockIssueSearchRepository, times(1)).save(testIssue);
    }

    @Test
    @Transactional
    public void createIssueWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = issueRepository.findAll().size();

        // Create the Issue with an existing ID
        issue.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restIssueMockMvc.perform(post("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(issue)))
            .andExpect(status().isBadRequest());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeCreate);

        // Validate the Issue in Elasticsearch
        verify(mockIssueSearchRepository, times(0)).save(issue);
    }


    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = issueRepository.findAll().size();
        // set the field null
        issue.setDescription(null);

        // Create the Issue, which fails.

        restIssueMockMvc.perform(post("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(issue)))
            .andExpect(status().isBadRequest());

        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateOfIssueIsRequired() throws Exception {
        int databaseSizeBeforeTest = issueRepository.findAll().size();
        // set the field null
        issue.setDateOfIssue(null);

        // Create the Issue, which fails.

        restIssueMockMvc.perform(post("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(issue)))
            .andExpect(status().isBadRequest());

        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = issueRepository.findAll().size();
        // set the field null
        issue.setStatus(null);

        // Create the Issue, which fails.

        restIssueMockMvc.perform(post("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(issue)))
            .andExpect(status().isBadRequest());

        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSolvedDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = issueRepository.findAll().size();
        // set the field null
        issue.setSolvedDate(null);

        // Create the Issue, which fails.

        restIssueMockMvc.perform(post("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(issue)))
            .andExpect(status().isBadRequest());

        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeOfIssueIsRequired() throws Exception {
        int databaseSizeBeforeTest = issueRepository.findAll().size();
        // set the field null
        issue.setTypeOfIssue(null);

        // Create the Issue, which fails.

        restIssueMockMvc.perform(post("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(issue)))
            .andExpect(status().isBadRequest());

        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllIssues() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get all the issueList
        restIssueMockMvc.perform(get("/api/issues?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(issue.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].dateOfIssue").value(hasItem(DEFAULT_DATE_OF_ISSUE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].solvedDate").value(hasItem(DEFAULT_SOLVED_DATE.toString())))
            .andExpect(jsonPath("$.[*].solvedBy").value(hasItem(DEFAULT_SOLVED_BY.toString())))
            .andExpect(jsonPath("$.[*].numberOfDays").value(hasItem(DEFAULT_NUMBER_OF_DAYS)))
            .andExpect(jsonPath("$.[*].typeOfIssue").value(hasItem(DEFAULT_TYPE_OF_ISSUE.toString())));
    }
    
    @Test
    @Transactional
    public void getIssue() throws Exception {
        // Initialize the database
        issueRepository.saveAndFlush(issue);

        // Get the issue
        restIssueMockMvc.perform(get("/api/issues/{id}", issue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(issue.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.dateOfIssue").value(DEFAULT_DATE_OF_ISSUE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.solvedDate").value(DEFAULT_SOLVED_DATE.toString()))
            .andExpect(jsonPath("$.solvedBy").value(DEFAULT_SOLVED_BY.toString()))
            .andExpect(jsonPath("$.numberOfDays").value(DEFAULT_NUMBER_OF_DAYS))
            .andExpect(jsonPath("$.typeOfIssue").value(DEFAULT_TYPE_OF_ISSUE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingIssue() throws Exception {
        // Get the issue
        restIssueMockMvc.perform(get("/api/issues/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIssue() throws Exception {
        // Initialize the database
        issueService.save(issue);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockIssueSearchRepository);

        int databaseSizeBeforeUpdate = issueRepository.findAll().size();

        // Update the issue
        Issue updatedIssue = issueRepository.findById(issue.getId()).get();
        // Disconnect from session so that the updates on updatedIssue are not directly saved in db
        em.detach(updatedIssue);
        updatedIssue
            .description(UPDATED_DESCRIPTION)
            .dateOfIssue(UPDATED_DATE_OF_ISSUE)
            .status(UPDATED_STATUS)
            .solvedDate(UPDATED_SOLVED_DATE)
            .solvedBy(UPDATED_SOLVED_BY)
            .numberOfDays(UPDATED_NUMBER_OF_DAYS)
            .typeOfIssue(UPDATED_TYPE_OF_ISSUE);

        restIssueMockMvc.perform(put("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedIssue)))
            .andExpect(status().isOk());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);
        Issue testIssue = issueList.get(issueList.size() - 1);
        assertThat(testIssue.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testIssue.getDateOfIssue()).isEqualTo(UPDATED_DATE_OF_ISSUE);
        assertThat(testIssue.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testIssue.getSolvedDate()).isEqualTo(UPDATED_SOLVED_DATE);
        assertThat(testIssue.getSolvedBy()).isEqualTo(UPDATED_SOLVED_BY);
        assertThat(testIssue.getNumberOfDays()).isEqualTo(UPDATED_NUMBER_OF_DAYS);
        assertThat(testIssue.getTypeOfIssue()).isEqualTo(UPDATED_TYPE_OF_ISSUE);

        // Validate the Issue in Elasticsearch
        verify(mockIssueSearchRepository, times(1)).save(testIssue);
    }

    @Test
    @Transactional
    public void updateNonExistingIssue() throws Exception {
        int databaseSizeBeforeUpdate = issueRepository.findAll().size();

        // Create the Issue

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restIssueMockMvc.perform(put("/api/issues")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(issue)))
            .andExpect(status().isBadRequest());

        // Validate the Issue in the database
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Issue in Elasticsearch
        verify(mockIssueSearchRepository, times(0)).save(issue);
    }

    @Test
    @Transactional
    public void deleteIssue() throws Exception {
        // Initialize the database
        issueService.save(issue);

        int databaseSizeBeforeDelete = issueRepository.findAll().size();

        // Delete the issue
        restIssueMockMvc.perform(delete("/api/issues/{id}", issue.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Issue> issueList = issueRepository.findAll();
        assertThat(issueList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Issue in Elasticsearch
        verify(mockIssueSearchRepository, times(1)).deleteById(issue.getId());
    }

    @Test
    @Transactional
    public void searchIssue() throws Exception {
        // Initialize the database
        issueService.save(issue);
        when(mockIssueSearchRepository.search(queryStringQuery("id:" + issue.getId())))
            .thenReturn(Collections.singletonList(issue));
        // Search the issue
        restIssueMockMvc.perform(get("/api/_search/issues?query=id:" + issue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(issue.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dateOfIssue").value(hasItem(DEFAULT_DATE_OF_ISSUE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].solvedDate").value(hasItem(DEFAULT_SOLVED_DATE.toString())))
            .andExpect(jsonPath("$.[*].solvedBy").value(hasItem(DEFAULT_SOLVED_BY)))
            .andExpect(jsonPath("$.[*].numberOfDays").value(hasItem(DEFAULT_NUMBER_OF_DAYS)))
            .andExpect(jsonPath("$.[*].typeOfIssue").value(hasItem(DEFAULT_TYPE_OF_ISSUE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Issue.class);
        Issue issue1 = new Issue();
        issue1.setId(1L);
        Issue issue2 = new Issue();
        issue2.setId(issue1.getId());
        assertThat(issue1).isEqualTo(issue2);
        issue2.setId(2L);
        assertThat(issue1).isNotEqualTo(issue2);
        issue1.setId(null);
        assertThat(issue1).isNotEqualTo(issue2);
    }
}
