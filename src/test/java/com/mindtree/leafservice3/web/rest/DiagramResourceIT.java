package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Diagram;
import com.mindtree.leafservice3.domain.Company;
import com.mindtree.leafservice3.repository.DiagramRepository;
import com.mindtree.leafservice3.repository.search.DiagramSearchRepository;
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
import java.util.Collections;
import java.util.List;

import static com.mindtree.leafservice3.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mindtree.leafservice3.domain.enumeration.DiagramCategory;
/**
 * Integration tests for the {@link DiagramResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class DiagramResourceIT {

    private static final DiagramCategory DEFAULT_CATEGORY = DiagramCategory.APPPORTFOLIO;
    private static final DiagramCategory UPDATED_CATEGORY = DiagramCategory.TECHNICALVIEW;

    private static final byte[] DEFAULT_PICTURE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PICTURE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PICTURE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PICTURE_CONTENT_TYPE = "image/png";

    @Autowired
    private DiagramRepository diagramRepository;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.DiagramSearchRepositoryMockConfiguration
     */
    @Autowired
    private DiagramSearchRepository mockDiagramSearchRepository;

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

    private MockMvc restDiagramMockMvc;

    private Diagram diagram;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DiagramResource diagramResource = new DiagramResource(diagramRepository, mockDiagramSearchRepository);
        this.restDiagramMockMvc = MockMvcBuilders.standaloneSetup(diagramResource)
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
    public static Diagram createEntity(EntityManager em) {
        Diagram diagram = new Diagram()
            .category(DEFAULT_CATEGORY)
            .picture(DEFAULT_PICTURE)
            .pictureContentType(DEFAULT_PICTURE_CONTENT_TYPE);
        // Add required entity
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            company = CompanyResourceIT.createEntity(em);
            em.persist(company);
            em.flush();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        diagram.setCompany(company);
        return diagram;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Diagram createUpdatedEntity(EntityManager em) {
        Diagram diagram = new Diagram()
            .category(UPDATED_CATEGORY)
            .picture(UPDATED_PICTURE)
            .pictureContentType(UPDATED_PICTURE_CONTENT_TYPE);
        // Add required entity
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            company = CompanyResourceIT.createUpdatedEntity(em);
            em.persist(company);
            em.flush();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        diagram.setCompany(company);
        return diagram;
    }

    @BeforeEach
    public void initTest() {
        diagram = createEntity(em);
    }

    @Test
    @Transactional
    public void createDiagram() throws Exception {
        int databaseSizeBeforeCreate = diagramRepository.findAll().size();

        // Create the Diagram
        restDiagramMockMvc.perform(post("/api/diagrams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diagram)))
            .andExpect(status().isCreated());

        // Validate the Diagram in the database
        List<Diagram> diagramList = diagramRepository.findAll();
        assertThat(diagramList).hasSize(databaseSizeBeforeCreate + 1);
        Diagram testDiagram = diagramList.get(diagramList.size() - 1);
        assertThat(testDiagram.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testDiagram.getPicture()).isEqualTo(DEFAULT_PICTURE);
        assertThat(testDiagram.getPictureContentType()).isEqualTo(DEFAULT_PICTURE_CONTENT_TYPE);

        // Validate the Diagram in Elasticsearch
        verify(mockDiagramSearchRepository, times(1)).save(testDiagram);
    }

    @Test
    @Transactional
    public void createDiagramWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = diagramRepository.findAll().size();

        // Create the Diagram with an existing ID
        diagram.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDiagramMockMvc.perform(post("/api/diagrams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diagram)))
            .andExpect(status().isBadRequest());

        // Validate the Diagram in the database
        List<Diagram> diagramList = diagramRepository.findAll();
        assertThat(diagramList).hasSize(databaseSizeBeforeCreate);

        // Validate the Diagram in Elasticsearch
        verify(mockDiagramSearchRepository, times(0)).save(diagram);
    }


    @Test
    @Transactional
    public void checkCategoryIsRequired() throws Exception {
        int databaseSizeBeforeTest = diagramRepository.findAll().size();
        // set the field null
        diagram.setCategory(null);

        // Create the Diagram, which fails.

        restDiagramMockMvc.perform(post("/api/diagrams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diagram)))
            .andExpect(status().isBadRequest());

        List<Diagram> diagramList = diagramRepository.findAll();
        assertThat(diagramList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDiagrams() throws Exception {
        // Initialize the database
        diagramRepository.saveAndFlush(diagram);

        // Get all the diagramList
        restDiagramMockMvc.perform(get("/api/diagrams?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(diagram.getId().intValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].pictureContentType").value(hasItem(DEFAULT_PICTURE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].picture").value(hasItem(Base64Utils.encodeToString(DEFAULT_PICTURE))));
    }
    
    @Test
    @Transactional
    public void getDiagram() throws Exception {
        // Initialize the database
        diagramRepository.saveAndFlush(diagram);

        // Get the diagram
        restDiagramMockMvc.perform(get("/api/diagrams/{id}", diagram.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(diagram.getId().intValue()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.pictureContentType").value(DEFAULT_PICTURE_CONTENT_TYPE))
            .andExpect(jsonPath("$.picture").value(Base64Utils.encodeToString(DEFAULT_PICTURE)));
    }

    @Test
    @Transactional
    public void getNonExistingDiagram() throws Exception {
        // Get the diagram
        restDiagramMockMvc.perform(get("/api/diagrams/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDiagram() throws Exception {
        // Initialize the database
        diagramRepository.saveAndFlush(diagram);

        int databaseSizeBeforeUpdate = diagramRepository.findAll().size();

        // Update the diagram
        Diagram updatedDiagram = diagramRepository.findById(diagram.getId()).get();
        // Disconnect from session so that the updates on updatedDiagram are not directly saved in db
        em.detach(updatedDiagram);
        updatedDiagram
            .category(UPDATED_CATEGORY)
            .picture(UPDATED_PICTURE)
            .pictureContentType(UPDATED_PICTURE_CONTENT_TYPE);

        restDiagramMockMvc.perform(put("/api/diagrams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDiagram)))
            .andExpect(status().isOk());

        // Validate the Diagram in the database
        List<Diagram> diagramList = diagramRepository.findAll();
        assertThat(diagramList).hasSize(databaseSizeBeforeUpdate);
        Diagram testDiagram = diagramList.get(diagramList.size() - 1);
        assertThat(testDiagram.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testDiagram.getPicture()).isEqualTo(UPDATED_PICTURE);
        assertThat(testDiagram.getPictureContentType()).isEqualTo(UPDATED_PICTURE_CONTENT_TYPE);

        // Validate the Diagram in Elasticsearch
        verify(mockDiagramSearchRepository, times(1)).save(testDiagram);
    }

    @Test
    @Transactional
    public void updateNonExistingDiagram() throws Exception {
        int databaseSizeBeforeUpdate = diagramRepository.findAll().size();

        // Create the Diagram

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDiagramMockMvc.perform(put("/api/diagrams")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(diagram)))
            .andExpect(status().isBadRequest());

        // Validate the Diagram in the database
        List<Diagram> diagramList = diagramRepository.findAll();
        assertThat(diagramList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Diagram in Elasticsearch
        verify(mockDiagramSearchRepository, times(0)).save(diagram);
    }

    @Test
    @Transactional
    public void deleteDiagram() throws Exception {
        // Initialize the database
        diagramRepository.saveAndFlush(diagram);

        int databaseSizeBeforeDelete = diagramRepository.findAll().size();

        // Delete the diagram
        restDiagramMockMvc.perform(delete("/api/diagrams/{id}", diagram.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Diagram> diagramList = diagramRepository.findAll();
        assertThat(diagramList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Diagram in Elasticsearch
        verify(mockDiagramSearchRepository, times(1)).deleteById(diagram.getId());
    }

    @Test
    @Transactional
    public void searchDiagram() throws Exception {
        // Initialize the database
        diagramRepository.saveAndFlush(diagram);
        when(mockDiagramSearchRepository.search(queryStringQuery("id:" + diagram.getId())))
            .thenReturn(Collections.singletonList(diagram));
        // Search the diagram
        restDiagramMockMvc.perform(get("/api/_search/diagrams?query=id:" + diagram.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(diagram.getId().intValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].pictureContentType").value(hasItem(DEFAULT_PICTURE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].picture").value(hasItem(Base64Utils.encodeToString(DEFAULT_PICTURE))));
    }
}
