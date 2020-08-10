package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.LeafService3App;
import com.mindtree.leafservice3.domain.Brand;
import com.mindtree.leafservice3.repository.BrandRepository;
import com.mindtree.leafservice3.repository.search.BrandSearchRepository;
import com.mindtree.leafservice3.service.BrandService;
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
 * Integration tests for the {@Link BrandResource} REST controller.
 */
@SpringBootTest(classes = LeafService3App.class)
public class BrandResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandService brandService;

    /**
     * This repository is mocked in the com.mindtree.leafservice3.repository.search test package.
     *
     * @see com.mindtree.leafservice3.repository.search.BrandSearchRepositoryMockConfiguration
     */
    @Autowired
    private BrandSearchRepository mockBrandSearchRepository;

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

    private MockMvc restBrandMockMvc;

    private Brand brand;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BrandResource brandResource = new BrandResource(brandService);
        this.restBrandMockMvc = MockMvcBuilders.standaloneSetup(brandResource)
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
    public static Brand createEntity(EntityManager em) {
        Brand brand = new Brand()
            .name(DEFAULT_NAME);
        return brand;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Brand createUpdatedEntity(EntityManager em) {
        Brand brand = new Brand()
            .name(UPDATED_NAME);
        return brand;
    }

    @BeforeEach
    public void initTest() {
        brand = createEntity(em);
    }

    @Test
    @Transactional
    public void createBrand() throws Exception {
        int databaseSizeBeforeCreate = brandRepository.findAll().size();

        // Create the Brand
        restBrandMockMvc.perform(post("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brand)))
            .andExpect(status().isCreated());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeCreate + 1);
        Brand testBrand = brandList.get(brandList.size() - 1);
        assertThat(testBrand.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Brand in Elasticsearch
        verify(mockBrandSearchRepository, times(1)).save(testBrand);
    }

    @Test
    @Transactional
    public void createBrandWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = brandRepository.findAll().size();

        // Create the Brand with an existing ID
        brand.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBrandMockMvc.perform(post("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brand)))
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeCreate);

        // Validate the Brand in Elasticsearch
        verify(mockBrandSearchRepository, times(0)).save(brand);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = brandRepository.findAll().size();
        // set the field null
        brand.setName(null);

        // Create the Brand, which fails.

        restBrandMockMvc.perform(post("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brand)))
            .andExpect(status().isBadRequest());

        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBrands() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList
        restBrandMockMvc.perform(get("/api/brands?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brand.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get the brand
        restBrandMockMvc.perform(get("/api/brands/{id}", brand.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(brand.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBrand() throws Exception {
        // Get the brand
        restBrandMockMvc.perform(get("/api/brands/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBrand() throws Exception {
        // Initialize the database
        brandService.save(brand);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockBrandSearchRepository);

        int databaseSizeBeforeUpdate = brandRepository.findAll().size();

        // Update the brand
        Brand updatedBrand = brandRepository.findById(brand.getId()).get();
        // Disconnect from session so that the updates on updatedBrand are not directly saved in db
        em.detach(updatedBrand);
        updatedBrand
            .name(UPDATED_NAME);

        restBrandMockMvc.perform(put("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBrand)))
            .andExpect(status().isOk());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
        Brand testBrand = brandList.get(brandList.size() - 1);
        assertThat(testBrand.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Brand in Elasticsearch
        verify(mockBrandSearchRepository, times(1)).save(testBrand);
    }

    @Test
    @Transactional
    public void updateNonExistingBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().size();

        // Create the Brand

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBrandMockMvc.perform(put("/api/brands")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brand)))
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Brand in Elasticsearch
        verify(mockBrandSearchRepository, times(0)).save(brand);
    }

    @Test
    @Transactional
    public void deleteBrand() throws Exception {
        // Initialize the database
        brandService.save(brand);

        int databaseSizeBeforeDelete = brandRepository.findAll().size();

        // Delete the brand
        restBrandMockMvc.perform(delete("/api/brands/{id}", brand.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Brand in Elasticsearch
        verify(mockBrandSearchRepository, times(1)).deleteById(brand.getId());
    }

    @Test
    @Transactional
    public void searchBrand() throws Exception {
        // Initialize the database
        brandService.save(brand);
        when(mockBrandSearchRepository.search(queryStringQuery("id:" + brand.getId())))
            .thenReturn(Collections.singletonList(brand));
        // Search the brand
        restBrandMockMvc.perform(get("/api/_search/brands?query=id:" + brand.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brand.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Brand.class);
        Brand brand1 = new Brand();
        brand1.setId(1L);
        Brand brand2 = new Brand();
        brand2.setId(brand1.getId());
        assertThat(brand1).isEqualTo(brand2);
        brand2.setId(2L);
        assertThat(brand1).isNotEqualTo(brand2);
        brand1.setId(null);
        assertThat(brand1).isNotEqualTo(brand2);
    }
}
