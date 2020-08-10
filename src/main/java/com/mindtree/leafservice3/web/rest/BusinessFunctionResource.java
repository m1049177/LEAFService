package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.BusinessFunction;
import com.mindtree.leafservice3.service.BusinessFunctionService;
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
 * REST controller for managing {@link com.mindtree.leafservice3.domain.BusinessFunction}.
 */
@RestController
@RequestMapping("/api")
public class BusinessFunctionResource {

    private final Logger log = LoggerFactory.getLogger(BusinessFunctionResource.class);

    private static final String ENTITY_NAME = "businessFunction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BusinessFunctionService businessFunctionService;

    public BusinessFunctionResource(BusinessFunctionService businessFunctionService) {
        this.businessFunctionService = businessFunctionService;
    }

    /**
     * {@code POST  /business-functions} : Create a new businessFunction.
     *
     * @param businessFunction the businessFunction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new businessFunction, or with status {@code 400 (Bad Request)} if the businessFunction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/business-functions")
    public ResponseEntity<BusinessFunction> createBusinessFunction(@Valid @RequestBody BusinessFunction businessFunction) throws URISyntaxException {
        log.debug("REST request to save BusinessFunction : {}", businessFunction);
        if (businessFunction.getId() != null) {
            throw new BadRequestAlertException("A new businessFunction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BusinessFunction result = businessFunctionService.save(businessFunction);
        return ResponseEntity.created(new URI("/api/business-functions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /business-functions} : Updates an existing businessFunction.
     *
     * @param businessFunction the businessFunction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessFunction,
     * or with status {@code 400 (Bad Request)} if the businessFunction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the businessFunction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/business-functions")
    public ResponseEntity<BusinessFunction> updateBusinessFunction(@Valid @RequestBody BusinessFunction businessFunction) throws URISyntaxException {
        log.debug("REST request to update BusinessFunction : {}", businessFunction);
        if (businessFunction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BusinessFunction result = businessFunctionService.save(businessFunction);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, businessFunction.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /business-functions} : get all the businessFunctions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businessFunctions in body.
     */
    @GetMapping("/business-functions")
    public List<BusinessFunction> getAllBusinessFunctions() {
        log.debug("REST request to get all BusinessFunctions");
        return businessFunctionService.findAll();
    }

    /**
     * {@code GET  /business-functions/:id} : get the "id" businessFunction.
     *
     * @param id the id of the businessFunction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the businessFunction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/business-functions/{id}")
    public ResponseEntity<BusinessFunction> getBusinessFunction(@PathVariable Long id) {
        log.debug("REST request to get BusinessFunction : {}", id);
        Optional<BusinessFunction> businessFunction = businessFunctionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(businessFunction);
    }

    /**
     * {@code DELETE  /business-functions/:id} : delete the "id" businessFunction.
     *
     * @param id the id of the businessFunction to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/business-functions/{id}")
    public ResponseEntity<Void> deleteBusinessFunction(@PathVariable Long id) {
        log.debug("REST request to delete BusinessFunction : {}", id);
        businessFunctionService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/business-functions?query=:query} : search for the businessFunction corresponding
     * to the query.
     *
     * @param query the query of the businessFunction search.
     * @return the result of the search.
     */
    @GetMapping("/_search/business-functions")
    public List<BusinessFunction> searchBusinessFunctions(@RequestParam String query) {
        log.debug("REST request to search BusinessFunctions for query {}", query);
        return businessFunctionService.search(query);
    }

}
