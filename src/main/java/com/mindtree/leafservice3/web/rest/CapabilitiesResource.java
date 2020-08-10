package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.Capabilities;
import com.mindtree.leafservice3.service.CapabilitiesService;
import com.mindtree.leafservice3.service.dto.ChartData;
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
 * REST controller for managing {@link com.mindtree.leafservice3.domain.Capabilities}.
 */
@RestController
@RequestMapping("/api")
public class CapabilitiesResource {

    private final Logger log = LoggerFactory.getLogger(CapabilitiesResource.class);

    private static final String ENTITY_NAME = "capabilities";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CapabilitiesService capabilitiesService;

    public CapabilitiesResource(CapabilitiesService capabilitiesService) {
        this.capabilitiesService = capabilitiesService;
    }

    /**
     * {@code POST  /capabilities} : Create a new capabilities.
     *
     * @param capabilities the capabilities to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new capabilities, or with status {@code 400 (Bad Request)} if the capabilities has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/capabilities")
    public ResponseEntity<Capabilities> createCapabilities(@Valid @RequestBody Capabilities capabilities) throws URISyntaxException {
        log.debug("REST request to save Capabilities : {}", capabilities);
        if (capabilities.getId() != null) {
            throw new BadRequestAlertException("A new capabilities cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Capabilities result = capabilitiesService.save(capabilities);
        return ResponseEntity.created(new URI("/api/capabilities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /capabilities} : Updates an existing capabilities.
     *
     * @param capabilities the capabilities to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated capabilities,
     * or with status {@code 400 (Bad Request)} if the capabilities is not valid,
     * or with status {@code 500 (Internal Server Error)} if the capabilities couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/capabilities")
    public ResponseEntity<Capabilities> updateCapabilities(@Valid @RequestBody Capabilities capabilities) throws URISyntaxException {
        log.debug("REST request to update Capabilities : {}", capabilities);
        if (capabilities.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Capabilities result = capabilitiesService.save(capabilities);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, capabilities.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /capabilities} : get all the capabilities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of capabilities in body.
     */
    @GetMapping("/capabilities")
    public List<Capabilities> getAllCapabilities() {
        log.debug("REST request to get all Capabilities");
        return capabilitiesService.findAll();
    }

    /**
     * {@code GET  /capabilities/:id} : get the "id" capabilities.
     *
     * @param id the id of the capabilities to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the capabilities, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/capabilities/{id}")
    public ResponseEntity<Capabilities> getCapabilities(@PathVariable Long id) {
        log.debug("REST request to get Capabilities : {}", id);
        Optional<Capabilities> capabilities = capabilitiesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(capabilities);
    }

    /**
     * {@code DELETE  /capabilities/:id} : delete the "id" capabilities.
     *
     * @param id the id of the capabilities to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/capabilities/{id}")
    public ResponseEntity<Void> deleteCapabilities(@PathVariable Long id) {
        log.debug("REST request to delete Capabilities : {}", id);
        capabilitiesService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/capabilities?query=:query} : search for the capabilities corresponding
     * to the query.
     *
     * @param query the query of the capabilities search.
     * @return the result of the search.
     */
    @GetMapping("/_search/capabilities")
    public List<Capabilities> searchCapabilities(@RequestParam String query) {
        log.debug("REST request to search Capabilities for query {}", query);
        return capabilitiesService.search(query);
    }

    @GetMapping("/getChartData/{company_id}")
    public List<ChartData> organizationalChartData(@PathVariable Long company_id) { 
        log.debug("REST request to get organizational chart data");
        return capabilitiesService.organizationalChartData(company_id);
    }

}
