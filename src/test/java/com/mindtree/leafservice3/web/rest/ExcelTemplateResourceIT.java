package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.ExcelTemplate;
import com.mindtree.leafservice3.repository.ExcelTemplateRepository;
import com.mindtree.leafservice3.repository.search.ExcelTemplateSearchRepository;
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

import com.mindtree.leafservice3.domain.enumeration.TemplateType;
/**
 * Integration tests for the {@Link ExcelTemplateResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class ExcelTemplateResourceIT {

    private static final byte[] DEFAULT_FILE_NAME = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE_NAME = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_FILE_NAME_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_NAME_CONTENT_TYPE = "image/png";

    private static final TemplateType DEFAULT_TYPE = TemplateType.APPPORTFOLIO;
    private static final TemplateType UPDATED_TYPE = TemplateType.TECHNICALVIEW;

    @Autowired
    private ExcelTemplateRepository excelTemplateRepository;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.ExcelTemplateSearchRepositoryMockConfiguration
     */
    @Autowired
    private ExcelTemplateSearchRepository mockExcelTemplateSearchRepository;

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

    private MockMvc restExcelTemplateMockMvc;

    private ExcelTemplate excelTemplate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ExcelTemplateResource excelTemplateResource = new ExcelTemplateResource(excelTemplateRepository, mockExcelTemplateSearchRepository);
        this.restExcelTemplateMockMvc = MockMvcBuilders.standaloneSetup(excelTemplateResource)
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
    public static ExcelTemplate createEntity(EntityManager em) {
        ExcelTemplate excelTemplate = new ExcelTemplate()
            .fileName(DEFAULT_FILE_NAME)
            .fileNameContentType(DEFAULT_FILE_NAME_CONTENT_TYPE)
            .type(DEFAULT_TYPE);
        return excelTemplate;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExcelTemplate createUpdatedEntity(EntityManager em) {
        ExcelTemplate excelTemplate = new ExcelTemplate()
            .fileName(UPDATED_FILE_NAME)
            .fileNameContentType(UPDATED_FILE_NAME_CONTENT_TYPE)
            .type(UPDATED_TYPE);
        return excelTemplate;
    }

    @BeforeEach
    public void initTest() {
        excelTemplate = createEntity(em);
    }

    @Test
    @Transactional
    public void createExcelTemplate() throws Exception {
        int databaseSizeBeforeCreate = excelTemplateRepository.findAll().size();

        // Create the ExcelTemplate
        restExcelTemplateMockMvc.perform(post("/api/excel-templates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(excelTemplate)))
            .andExpect(status().isCreated());

        // Validate the ExcelTemplate in the database
        List<ExcelTemplate> excelTemplateList = excelTemplateRepository.findAll();
        assertThat(excelTemplateList).hasSize(databaseSizeBeforeCreate + 1);
        ExcelTemplate testExcelTemplate = excelTemplateList.get(excelTemplateList.size() - 1);
        assertThat(testExcelTemplate.getFileName()).isEqualTo(DEFAULT_FILE_NAME);
        assertThat(testExcelTemplate.getFileNameContentType()).isEqualTo(DEFAULT_FILE_NAME_CONTENT_TYPE);
        assertThat(testExcelTemplate.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the ExcelTemplate in Elasticsearch
        verify(mockExcelTemplateSearchRepository, times(1)).save(testExcelTemplate);
    }

    @Test
    @Transactional
    public void createExcelTemplateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = excelTemplateRepository.findAll().size();

        // Create the ExcelTemplate with an existing ID
        excelTemplate.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restExcelTemplateMockMvc.perform(post("/api/excel-templates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(excelTemplate)))
            .andExpect(status().isBadRequest());

        // Validate the ExcelTemplate in the database
        List<ExcelTemplate> excelTemplateList = excelTemplateRepository.findAll();
        assertThat(excelTemplateList).hasSize(databaseSizeBeforeCreate);

        // Validate the ExcelTemplate in Elasticsearch
        verify(mockExcelTemplateSearchRepository, times(0)).save(excelTemplate);
    }


    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = excelTemplateRepository.findAll().size();
        // set the field null
        excelTemplate.setType(null);

        // Create the ExcelTemplate, which fails.

        restExcelTemplateMockMvc.perform(post("/api/excel-templates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(excelTemplate)))
            .andExpect(status().isBadRequest());

        List<ExcelTemplate> excelTemplateList = excelTemplateRepository.findAll();
        assertThat(excelTemplateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllExcelTemplates() throws Exception {
        // Initialize the database
        excelTemplateRepository.saveAndFlush(excelTemplate);

        // Get all the excelTemplateList
        restExcelTemplateMockMvc.perform(get("/api/excel-templates?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(excelTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileNameContentType").value(hasItem(DEFAULT_FILE_NAME_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE_NAME))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getExcelTemplate() throws Exception {
        // Initialize the database
        excelTemplateRepository.saveAndFlush(excelTemplate);

        // Get the excelTemplate
        restExcelTemplateMockMvc.perform(get("/api/excel-templates/{id}", excelTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(excelTemplate.getId().intValue()))
            .andExpect(jsonPath("$.fileNameContentType").value(DEFAULT_FILE_NAME_CONTENT_TYPE))
            .andExpect(jsonPath("$.fileName").value(Base64Utils.encodeToString(DEFAULT_FILE_NAME)))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingExcelTemplate() throws Exception {
        // Get the excelTemplate
        restExcelTemplateMockMvc.perform(get("/api/excel-templates/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateExcelTemplate() throws Exception {
        // Initialize the database
        excelTemplateRepository.saveAndFlush(excelTemplate);

        int databaseSizeBeforeUpdate = excelTemplateRepository.findAll().size();

        // Update the excelTemplate
        ExcelTemplate updatedExcelTemplate = excelTemplateRepository.findById(excelTemplate.getId()).get();
        // Disconnect from session so that the updates on updatedExcelTemplate are not directly saved in db
        em.detach(updatedExcelTemplate);
        updatedExcelTemplate
            .fileName(UPDATED_FILE_NAME)
            .fileNameContentType(UPDATED_FILE_NAME_CONTENT_TYPE)
            .type(UPDATED_TYPE);

        restExcelTemplateMockMvc.perform(put("/api/excel-templates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedExcelTemplate)))
            .andExpect(status().isOk());

        // Validate the ExcelTemplate in the database
        List<ExcelTemplate> excelTemplateList = excelTemplateRepository.findAll();
        assertThat(excelTemplateList).hasSize(databaseSizeBeforeUpdate);
        ExcelTemplate testExcelTemplate = excelTemplateList.get(excelTemplateList.size() - 1);
        assertThat(testExcelTemplate.getFileName()).isEqualTo(UPDATED_FILE_NAME);
        assertThat(testExcelTemplate.getFileNameContentType()).isEqualTo(UPDATED_FILE_NAME_CONTENT_TYPE);
        assertThat(testExcelTemplate.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the ExcelTemplate in Elasticsearch
        verify(mockExcelTemplateSearchRepository, times(1)).save(testExcelTemplate);
    }

    @Test
    @Transactional
    public void updateNonExistingExcelTemplate() throws Exception {
        int databaseSizeBeforeUpdate = excelTemplateRepository.findAll().size();

        // Create the ExcelTemplate

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExcelTemplateMockMvc.perform(put("/api/excel-templates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(excelTemplate)))
            .andExpect(status().isBadRequest());

        // Validate the ExcelTemplate in the database
        List<ExcelTemplate> excelTemplateList = excelTemplateRepository.findAll();
        assertThat(excelTemplateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ExcelTemplate in Elasticsearch
        verify(mockExcelTemplateSearchRepository, times(0)).save(excelTemplate);
    }

    @Test
    @Transactional
    public void deleteExcelTemplate() throws Exception {
        // Initialize the database
        excelTemplateRepository.saveAndFlush(excelTemplate);

        int databaseSizeBeforeDelete = excelTemplateRepository.findAll().size();

        // Delete the excelTemplate
        restExcelTemplateMockMvc.perform(delete("/api/excel-templates/{id}", excelTemplate.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ExcelTemplate> excelTemplateList = excelTemplateRepository.findAll();
        assertThat(excelTemplateList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ExcelTemplate in Elasticsearch
        verify(mockExcelTemplateSearchRepository, times(1)).deleteById(excelTemplate.getId());
    }

    @Test
    @Transactional
    public void searchExcelTemplate() throws Exception {
        // Initialize the database
        excelTemplateRepository.saveAndFlush(excelTemplate);
        when(mockExcelTemplateSearchRepository.search(queryStringQuery("id:" + excelTemplate.getId())))
            .thenReturn(Collections.singletonList(excelTemplate));
        // Search the excelTemplate
        restExcelTemplateMockMvc.perform(get("/api/_search/excel-templates?query=id:" + excelTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(excelTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].fileNameContentType").value(hasItem(DEFAULT_FILE_NAME_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].fileName").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE_NAME))))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExcelTemplate.class);
        ExcelTemplate excelTemplate1 = new ExcelTemplate();
        excelTemplate1.setId(1L);
        ExcelTemplate excelTemplate2 = new ExcelTemplate();
        excelTemplate2.setId(excelTemplate1.getId());
        assertThat(excelTemplate1).isEqualTo(excelTemplate2);
        excelTemplate2.setId(2L);
        assertThat(excelTemplate1).isNotEqualTo(excelTemplate2);
        excelTemplate1.setId(null);
        assertThat(excelTemplate1).isNotEqualTo(excelTemplate2);
    }
}
