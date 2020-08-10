package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.ExcelTemplate;
import com.mindtree.leafservice3.repository.ExcelTemplateRepository;
import com.mindtree.leafservice3.repository.search.ExcelTemplateSearchRepository;
import com.mindtree.leafservice3.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.mindtree.leafservice3.domain.ExcelTemplate}.
 */
@RestController
@RequestMapping("/api")
public class ExcelTemplateResource {

    private final Logger log = LoggerFactory.getLogger(ExcelTemplateResource.class);

    private static final String ENTITY_NAME = "excelTemplate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExcelTemplateRepository excelTemplateRepository;

    private final ExcelTemplateSearchRepository excelTemplateSearchRepository;

    public ExcelTemplateResource(ExcelTemplateRepository excelTemplateRepository, ExcelTemplateSearchRepository excelTemplateSearchRepository) {
        this.excelTemplateRepository = excelTemplateRepository;
        this.excelTemplateSearchRepository = excelTemplateSearchRepository;
    }

    /**
     * {@code POST  /excel-templates} : Create a new excelTemplate.
     *
     * @param excelTemplate the excelTemplate to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new excelTemplate, or with status {@code 400 (Bad Request)} if the excelTemplate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/excel-templates")
    public ResponseEntity<ExcelTemplate> createExcelTemplate(@Valid @RequestBody ExcelTemplate excelTemplate) throws URISyntaxException {
        log.debug("REST request to save ExcelTemplate : {}", excelTemplate);
        if (excelTemplate.getId() != null) {
            throw new BadRequestAlertException("A new excelTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ExcelTemplate result = excelTemplateRepository.save(excelTemplate);
        excelTemplateSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/excel-templates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /excel-templates} : Updates an existing excelTemplate.
     *
     * @param excelTemplate the excelTemplate to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated excelTemplate,
     * or with status {@code 400 (Bad Request)} if the excelTemplate is not valid,
     * or with status {@code 500 (Internal Server Error)} if the excelTemplate couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/excel-templates")
    public ResponseEntity<ExcelTemplate> updateExcelTemplate(@Valid @RequestBody ExcelTemplate excelTemplate) throws URISyntaxException {
        log.debug("REST request to update ExcelTemplate : {}", excelTemplate);
        if (excelTemplate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ExcelTemplate result = excelTemplateRepository.save(excelTemplate);
        excelTemplateSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, excelTemplate.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /excel-templates} : get all the excelTemplates.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of excelTemplates in body.
     */
    @GetMapping("/excel-templates")
    public List<ExcelTemplate> getAllExcelTemplates() {
        log.debug("REST request to get all ExcelTemplates");
        return excelTemplateRepository.findAll();
    }

    /**
     * {@code GET  /excel-templates/:id} : get the "id" excelTemplate.
     *
     * @param id the id of the excelTemplate to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the excelTemplate, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/excel-templates/{id}")
    public ResponseEntity<ExcelTemplate> getExcelTemplate(@PathVariable Long id) {
        log.debug("REST request to get ExcelTemplate : {}", id);
        Optional<ExcelTemplate> excelTemplate = excelTemplateRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(excelTemplate);
    }

    /**
     * {@code DELETE  /excel-templates/:id} : delete the "id" excelTemplate.
     *
     * @param id the id of the excelTemplate to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/excel-templates/{id}")
    public ResponseEntity<Void> deleteExcelTemplate(@PathVariable Long id) {
        log.debug("REST request to delete ExcelTemplate : {}", id);
        excelTemplateRepository.deleteById(id);
        excelTemplateSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/excel-templates?query=:query} : search for the excelTemplate corresponding
     * to the query.
     *
     * @param query the query of the excelTemplate search.
     * @return the result of the search.
     */
    @GetMapping("/_search/excel-templates")
    public List<ExcelTemplate> searchExcelTemplates(@RequestParam String query) {
        log.debug("REST request to search ExcelTemplates for query {}", query);
        return StreamSupport
            .stream(excelTemplateSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
