package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.UploadExcel;
import com.mindtree.leafservice3.service.UploadExcelService;
import com.mindtree.leafservice3.web.rest.errors.BadRequestAlertException;
import org.springframework.http.MediaType;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.io.IOException;

/**
 * REST controller for managing
 * {@link com.mindtree.leafservice3.domain.UploadExcel}.
 */
@RestController
@RequestMapping("/api")
public class UploadExcelResource {

    private final Logger log = LoggerFactory.getLogger(UploadExcelResource.class);

    private static final String ENTITY_NAME = "uploadExcel";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UploadExcelService uploadExcelService;

    public UploadExcelResource(UploadExcelService uploadExcelService) {
        this.uploadExcelService = uploadExcelService;

    }

    /**
     * {@code PUT  /upload-excels} : Updates an existing uploadExcel.
     *
     * @param uploadExcel the uploadExcel to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated uploadExcel, or with status {@code 400 (Bad Request)} if
     *         the uploadExcel is not valid, or with status
     *         {@code 500 (Internal Server Error)} if the uploadExcel couldn't be
     *         updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/upload-excels")
    public ResponseEntity<UploadExcel> updateUploadExcel(@Valid @RequestBody UploadExcel uploadExcel)
            throws URISyntaxException {
        log.debug("REST request to update UploadExcel : {}", uploadExcel);
        if (uploadExcel.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UploadExcel result = uploadExcelService.save(uploadExcel);
        return ResponseEntity.ok().headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, uploadExcel.getId().toString()))
                .body(result);
    }

    /**
     * {@code GET  /upload-excels} : get all the uploadExcels.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of uploadExcels in body.
     */
    @GetMapping("/upload-excels")
    public List<UploadExcel> getAllUploadExcels() {
        log.debug("REST request to get all UploadExcels");
        return uploadExcelService.findAll();
    }

    /**
     * {@code GET  /upload-excels/:id} : get the "id" uploadExcel.
     *
     * @param id the id of the uploadExcel to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the uploadExcel, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/upload-excels/{id}")
    public ResponseEntity<UploadExcel> getUploadExcel(@PathVariable Long id) {
        log.debug("REST request to get UploadExcel : {}", id);
        Optional<UploadExcel> uploadExcel = uploadExcelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(uploadExcel);
    }

    /**
     * {@code DELETE  /upload-excels/:id} : delete the "id" uploadExcel.
     *
     * @param id the id of the uploadExcel to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/upload-excels/{id}")
    public ResponseEntity<Void> deleteUploadExcel(@PathVariable Long id) {
        log.debug("REST request to delete UploadExcel : {}", id);
        uploadExcelService.delete(id);
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                .build();
    }

    /**
     * {@code SEARCH  /_search/upload-excels?query=:query} : search for the
     * uploadExcel corresponding to the query.
     *
     * @param query the query of the uploadExcel search.
     * @return the result of the search.
     */
    @GetMapping("/_search/upload-excels")
    public List<UploadExcel> searchUploadExcels(@RequestParam String query) {
        log.debug("REST request to search UploadExcels for query {}", query);
        return uploadExcelService.search(query);
    }

    @PostMapping(path = "/upload-excels/functionalView/{company_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String functionalView(@PathVariable Long company_id, @RequestParam("file") MultipartFile readExcelDataFile) throws IOException {
        log.debug("REST request to Upload excel for functional data for company", company_id);
        return uploadExcelService.functionDataUpload(company_id, readExcelDataFile);

    }

    @PostMapping(path = "/upload-excels/appView/{company_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String appView(@PathVariable Long company_id, @RequestParam("file") MultipartFile readExcelDataFile) throws IOException {
        log.debug("REST request to Upload excel for app portfolio data particular company", company_id);
        return uploadExcelService.appPortfolioUpload(company_id, readExcelDataFile);

    }

}
