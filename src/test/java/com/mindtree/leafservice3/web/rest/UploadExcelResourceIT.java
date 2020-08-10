package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.UploadExcel;
import com.mindtree.leafservice3.domain.Company;
import com.mindtree.leafservice3.repository.ApplicationRepository;
import com.mindtree.leafservice3.repository.ExpenditureRepository;
import com.mindtree.leafservice3.repository.SpendRepository;
import com.mindtree.leafservice3.repository.TechnologyRepository;
import com.mindtree.leafservice3.repository.TechnologyStackRepository;
import com.mindtree.leafservice3.repository.UploadExcelRepository;
import com.mindtree.leafservice3.repository.search.UploadExcelSearchRepository;
import com.mindtree.leafservice3.service.UploadExcelService;
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

import com.mindtree.leafservice3.domain.enumeration.UploadExcelType;

/**
 * Integration tests for the {@Link UploadExcelResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class UploadExcelResourceIT {

    private static final byte[] DEFAULT_NAME = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_NAME = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_NAME_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_NAME_CONTENT_TYPE = "image/png";

    private static final UploadExcelType DEFAULT_TYPE = UploadExcelType.APPPORTFOLIO;
    private static final UploadExcelType UPDATED_TYPE = UploadExcelType.TECHNICALVIEW;

    @Autowired
    private UploadExcelRepository uploadExcelRepository;

    @Autowired
    private UploadExcelService uploadExcelService;


    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.UploadExcelSearchRepositoryMockConfiguration
     */
    @Autowired
    private UploadExcelSearchRepository mockUploadExcelSearchRepository;

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

    private MockMvc restUploadExcelMockMvc;

    private UploadExcel uploadExcel;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UploadExcelResource uploadExcelResource = new UploadExcelResource(uploadExcelService);
        this.restUploadExcelMockMvc = MockMvcBuilders.standaloneSetup(uploadExcelResource)
                .setCustomArgumentResolvers(pageableArgumentResolver).setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService()).setMessageConverters(jacksonMessageConverter)
                .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it, if
     * they test an entity which requires the current entity.
     */
    public static UploadExcel createEntity(EntityManager em) {
        UploadExcel uploadExcel = new UploadExcel().name(DEFAULT_NAME).nameContentType(DEFAULT_NAME_CONTENT_TYPE)
                .type(DEFAULT_TYPE);
        // Add required entity
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            company = CompanyResourceIT.createEntity(em);
            em.persist(company);
            em.flush();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        uploadExcel.setCompany(company);
        return uploadExcel;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it, if
     * they test an entity which requires the current entity.
     */
    public static UploadExcel createUpdatedEntity(EntityManager em) {
        UploadExcel uploadExcel = new UploadExcel().name(UPDATED_NAME).nameContentType(UPDATED_NAME_CONTENT_TYPE)
                .type(UPDATED_TYPE);
        // Add required entity
        Company company;
        if (TestUtil.findAll(em, Company.class).isEmpty()) {
            company = CompanyResourceIT.createUpdatedEntity(em);
            em.persist(company);
            em.flush();
        } else {
            company = TestUtil.findAll(em, Company.class).get(0);
        }
        uploadExcel.setCompany(company);
        return uploadExcel;
    }

    @BeforeEach
    public void initTest() {
        uploadExcel = createEntity(em);
    }

    @Test
    @Transactional
    public void createUploadExcel() throws Exception {
        int databaseSizeBeforeCreate = uploadExcelRepository.findAll().size();

        // Create the UploadExcel
        restUploadExcelMockMvc.perform(post("/api/upload-excels").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(uploadExcel))).andExpect(status().isCreated());

        // Validate the UploadExcel in the database
        List<UploadExcel> uploadExcelList = uploadExcelRepository.findAll();
        assertThat(uploadExcelList).hasSize(databaseSizeBeforeCreate + 1);
        UploadExcel testUploadExcel = uploadExcelList.get(uploadExcelList.size() - 1);
        assertThat(testUploadExcel.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUploadExcel.getNameContentType()).isEqualTo(DEFAULT_NAME_CONTENT_TYPE);
        assertThat(testUploadExcel.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the UploadExcel in Elasticsearch
        verify(mockUploadExcelSearchRepository, times(1)).save(testUploadExcel);
    }

    @Test
    @Transactional
    public void createUploadExcelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = uploadExcelRepository.findAll().size();

        // Create the UploadExcel with an existing ID
        uploadExcel.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUploadExcelMockMvc.perform(post("/api/upload-excels").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(uploadExcel))).andExpect(status().isBadRequest());

        // Validate the UploadExcel in the database
        List<UploadExcel> uploadExcelList = uploadExcelRepository.findAll();
        assertThat(uploadExcelList).hasSize(databaseSizeBeforeCreate);

        // Validate the UploadExcel in Elasticsearch
        verify(mockUploadExcelSearchRepository, times(0)).save(uploadExcel);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = uploadExcelRepository.findAll().size();
        // set the field null
        uploadExcel.setType(null);

        // Create the UploadExcel, which fails.

        restUploadExcelMockMvc.perform(post("/api/upload-excels").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(uploadExcel))).andExpect(status().isBadRequest());

        List<UploadExcel> uploadExcelList = uploadExcelRepository.findAll();
        assertThat(uploadExcelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUploadExcels() throws Exception {
        // Initialize the database
        uploadExcelRepository.saveAndFlush(uploadExcel);

        // Get all the uploadExcelList
        restUploadExcelMockMvc.perform(get("/api/upload-excels?sort=id,desc")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(uploadExcel.getId().intValue())))
                .andExpect(jsonPath("$.[*].nameContentType").value(hasItem(DEFAULT_NAME_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].name").value(hasItem(Base64Utils.encodeToString(DEFAULT_NAME))))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getUploadExcel() throws Exception {
        // Initialize the database
        uploadExcelRepository.saveAndFlush(uploadExcel);

        // Get the uploadExcel
        restUploadExcelMockMvc.perform(get("/api/upload-excels/{id}", uploadExcel.getId())).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(uploadExcel.getId().intValue()))
                .andExpect(jsonPath("$.nameContentType").value(DEFAULT_NAME_CONTENT_TYPE))
                .andExpect(jsonPath("$.name").value(Base64Utils.encodeToString(DEFAULT_NAME)))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUploadExcel() throws Exception {
        // Get the uploadExcel
        restUploadExcelMockMvc.perform(get("/api/upload-excels/{id}", Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUploadExcel() throws Exception {
        // Initialize the database
        uploadExcelService.save(uploadExcel);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockUploadExcelSearchRepository);

        int databaseSizeBeforeUpdate = uploadExcelRepository.findAll().size();

        // Update the uploadExcel
        UploadExcel updatedUploadExcel = uploadExcelRepository.findById(uploadExcel.getId()).get();
        // Disconnect from session so that the updates on updatedUploadExcel are not
        // directly saved in db
        em.detach(updatedUploadExcel);
        updatedUploadExcel.name(UPDATED_NAME).nameContentType(UPDATED_NAME_CONTENT_TYPE).type(UPDATED_TYPE);

        restUploadExcelMockMvc.perform(put("/api/upload-excels").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedUploadExcel))).andExpect(status().isOk());

        // Validate the UploadExcel in the database
        List<UploadExcel> uploadExcelList = uploadExcelRepository.findAll();
        assertThat(uploadExcelList).hasSize(databaseSizeBeforeUpdate);
        UploadExcel testUploadExcel = uploadExcelList.get(uploadExcelList.size() - 1);
        assertThat(testUploadExcel.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUploadExcel.getNameContentType()).isEqualTo(UPDATED_NAME_CONTENT_TYPE);
        assertThat(testUploadExcel.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the UploadExcel in Elasticsearch
        verify(mockUploadExcelSearchRepository, times(1)).save(testUploadExcel);
    }

    @Test
    @Transactional
    public void updateNonExistingUploadExcel() throws Exception {
        int databaseSizeBeforeUpdate = uploadExcelRepository.findAll().size();

        // Create the UploadExcel

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUploadExcelMockMvc.perform(put("/api/upload-excels").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(uploadExcel))).andExpect(status().isBadRequest());

        // Validate the UploadExcel in the database
        List<UploadExcel> uploadExcelList = uploadExcelRepository.findAll();
        assertThat(uploadExcelList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UploadExcel in Elasticsearch
        verify(mockUploadExcelSearchRepository, times(0)).save(uploadExcel);
    }

    @Test
    @Transactional
    public void deleteUploadExcel() throws Exception {
        // Initialize the database
        uploadExcelService.save(uploadExcel);

        int databaseSizeBeforeDelete = uploadExcelRepository.findAll().size();

        // Delete the uploadExcel
        restUploadExcelMockMvc
                .perform(delete("/api/upload-excels/{id}", uploadExcel.getId()).accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UploadExcel> uploadExcelList = uploadExcelRepository.findAll();
        assertThat(uploadExcelList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UploadExcel in Elasticsearch
        verify(mockUploadExcelSearchRepository, times(1)).deleteById(uploadExcel.getId());
    }

    @Test
    @Transactional
    public void searchUploadExcel() throws Exception {
        // Initialize the database
        uploadExcelService.save(uploadExcel);
        when(mockUploadExcelSearchRepository.search(queryStringQuery("id:" + uploadExcel.getId())))
                .thenReturn(Collections.singletonList(uploadExcel));
        // Search the uploadExcel
        restUploadExcelMockMvc.perform(get("/api/_search/upload-excels?query=id:" + uploadExcel.getId()))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(uploadExcel.getId().intValue())))
                .andExpect(jsonPath("$.[*].nameContentType").value(hasItem(DEFAULT_NAME_CONTENT_TYPE)))
                .andExpect(jsonPath("$.[*].name").value(hasItem(Base64Utils.encodeToString(DEFAULT_NAME))))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UploadExcel.class);
        UploadExcel uploadExcel1 = new UploadExcel();
        uploadExcel1.setId(1L);
        UploadExcel uploadExcel2 = new UploadExcel();
        uploadExcel2.setId(uploadExcel1.getId());
        assertThat(uploadExcel1).isEqualTo(uploadExcel2);
        uploadExcel2.setId(2L);
        assertThat(uploadExcel1).isNotEqualTo(uploadExcel2);
        uploadExcel1.setId(null);
        assertThat(uploadExcel1).isNotEqualTo(uploadExcel2);
    }
}
