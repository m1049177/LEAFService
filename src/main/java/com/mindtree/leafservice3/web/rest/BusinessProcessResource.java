package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.BusinessProcess;
import com.mindtree.leafservice3.service.BusinessProcessService;
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
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.mindtree.leafservice3.domain.BusinessProcess}.
 */
@RestController
@RequestMapping("/api")
public class BusinessProcessResource {

    private final Logger log = LoggerFactory.getLogger(BusinessProcessResource.class);

    private static final String ENTITY_NAME = "businessProcess";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BusinessProcessService businessProcessService;

    public BusinessProcessResource(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }

    /**
     * {@code POST  /business-processes} : Create a new businessProcess.
     *
     * @param businessProcess the businessProcess to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new businessProcess, or with status {@code 400 (Bad Request)} if the businessProcess has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/business-processes")
    public ResponseEntity<BusinessProcess> createBusinessProcess(@Valid @RequestBody BusinessProcess businessProcess) throws URISyntaxException {
        log.debug("REST request to save BusinessProcess : {}", businessProcess);
        if (businessProcess.getId() != null) {
            throw new BadRequestAlertException("A new businessProcess cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BusinessProcess result = businessProcessService.save(businessProcess);
        return ResponseEntity.created(new URI("/api/business-processes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /business-processes} : Updates an existing businessProcess.
     *
     * @param businessProcess the businessProcess to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessProcess,
     * or with status {@code 400 (Bad Request)} if the businessProcess is not valid,
     * or with status {@code 500 (Internal Server Error)} if the businessProcess couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/business-processes")
    public ResponseEntity<BusinessProcess> updateBusinessProcess(@Valid @RequestBody BusinessProcess businessProcess) throws URISyntaxException {
        log.debug("REST request to update BusinessProcess : {}", businessProcess);
        if (businessProcess.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BusinessProcess result = businessProcessService.save(businessProcess);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, businessProcess.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /business-processes} : get all the businessProcesses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businessProcesses in body.
     */
    @GetMapping("/business-processes")
    public List<BusinessProcess> getAllBusinessProcesses() {
        log.debug("REST request to get all BusinessProcesses");
        return businessProcessService.findAll();
    }

    /**
     * {@code GET  /business-processes/:id} : get the "id" businessProcess.
     *
     * @param id the id of the businessProcess to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the businessProcess, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/business-processes/{id}")
    public ResponseEntity<BusinessProcess> getBusinessProcess(@PathVariable Long id) {
        log.debug("REST request to get BusinessProcess : {}", id);
        Optional<BusinessProcess> businessProcess = businessProcessService.findOne(id);
        return ResponseUtil.wrapOrNotFound(businessProcess);
    }

    /**
     * {@code DELETE  /business-processes/:id} : delete the "id" businessProcess.
     *
     * @param id the id of the businessProcess to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/business-processes/{id}")
    public ResponseEntity<Void> deleteBusinessProcess(@PathVariable Long id) {
        log.debug("REST request to delete BusinessProcess : {}", id);
        businessProcessService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/business-processes?query=:query} : search for the businessProcess corresponding
     * to the query.
     *
     * @param query the query of the businessProcess search.
     * @return the result of the search.
     */
    @GetMapping("/_search/business-processes")
    public List<BusinessProcess> searchBusinessProcesses(@RequestParam String query) {
        log.debug("REST request to search BusinessProcesses for query {}", query);
        return businessProcessService.search(query);
    }

}
