package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.Integration;
import com.mindtree.leafservice3.service.IntegrationService;
import com.mindtree.leafservice3.service.dto.IntegrationData;
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
 * REST controller for managing {@link com.mindtree.leafservice3.domain.Integration}.
 */
@RestController
@RequestMapping("/api")
public class IntegrationResource {

    private final Logger log = LoggerFactory.getLogger(IntegrationResource.class);

    private static final String ENTITY_NAME = "integration";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IntegrationService integrationService;

    public IntegrationResource(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    /**
     * {@code POST  /integrations} : Create a new integration.
     *
     * @param integration the integration to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new integration, or with status {@code 400 (Bad Request)} if the integration has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/integrations")
    public ResponseEntity<Integration> createIntegration(@Valid @RequestBody Integration integration) throws URISyntaxException {
        log.debug("REST request to save Integration : {}", integration);
        if (integration.getId() != null) {
            throw new BadRequestAlertException("A new integration cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Integration result = integrationService.save(integration);
        return ResponseEntity.created(new URI("/api/integrations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /integrations} : Updates an existing integration.
     *
     * @param integration the integration to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated integration,
     * or with status {@code 400 (Bad Request)} if the integration is not valid,
     * or with status {@code 500 (Internal Server Error)} if the integration couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/integrations")
    public ResponseEntity<Integration> updateIntegration(@Valid @RequestBody Integration integration) throws URISyntaxException {
        log.debug("REST request to update Integration : {}", integration);
        if (integration.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Integration result = integrationService.save(integration);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, integration.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /integrations} : get all the integrations.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of integrations in body.
     */
    @GetMapping("/integrations")
    public List<Integration> getAllIntegrations() {
        log.debug("REST request to get all Integrations");
        return integrationService.findAll();
    }

    /**
     * {@code GET  /integrations/:id} : get the "id" integration.
     *
     * @param id the id of the integration to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the integration, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/integrations/{id}")
    public ResponseEntity<Integration> getIntegration(@PathVariable Long id) {
        log.debug("REST request to get Integration : {}", id);
        Optional<Integration> integration = integrationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(integration);
    }

    /**
     * {@code DELETE  /integrations/:id} : delete the "id" integration.
     *
     * @param id the id of the integration to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/integrations/{id}")
    public ResponseEntity<Void> deleteIntegration(@PathVariable Long id) {
        log.debug("REST request to delete Integration : {}", id);
        integrationService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/integrations?query=:query} : search for the integration corresponding
     * to the query.
     *
     * @param query the query of the integration search.
     * @return the result of the search.
     */
    @GetMapping("/_search/integrations")
    public List<Integration> searchIntegrations(@RequestParam String query) {
        log.debug("REST request to search Integrations for query {}", query);
        return integrationService.search(query);
    }

    @GetMapping("/integrations/getIntegrationData/{app_id}")
    public IntegrationData getIntegrationData(@PathVariable Long app_id) {
        return integrationService.getIntegrationData(app_id);
    }
}
