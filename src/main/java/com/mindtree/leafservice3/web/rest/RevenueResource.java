package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.Revenue;
import com.mindtree.leafservice3.service.RevenueService;
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
 * REST controller for managing {@link com.mindtree.leafservice3.domain.Revenue}.
 */
@RestController
@RequestMapping("/api")
public class RevenueResource {

    private final Logger log = LoggerFactory.getLogger(RevenueResource.class);

    private static final String ENTITY_NAME = "revenue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RevenueService revenueService;

    public RevenueResource(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    /**
     * {@code POST  /revenues} : Create a new revenue.
     *
     * @param revenue the revenue to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new revenue, or with status {@code 400 (Bad Request)} if the revenue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/revenues")
    public ResponseEntity<Revenue> createRevenue(@Valid @RequestBody Revenue revenue) throws URISyntaxException {
        log.debug("REST request to save Revenue : {}", revenue);
        if (revenue.getId() != null) {
            throw new BadRequestAlertException("A new revenue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Revenue result = revenueService.save(revenue);
        return ResponseEntity.created(new URI("/api/revenues/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /revenues} : Updates an existing revenue.
     *
     * @param revenue the revenue to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated revenue,
     * or with status {@code 400 (Bad Request)} if the revenue is not valid,
     * or with status {@code 500 (Internal Server Error)} if the revenue couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/revenues")
    public ResponseEntity<Revenue> updateRevenue(@Valid @RequestBody Revenue revenue) throws URISyntaxException {
        log.debug("REST request to update Revenue : {}", revenue);
        if (revenue.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Revenue result = revenueService.save(revenue);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, revenue.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /revenues} : get all the revenues.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of revenues in body.
     */
    @GetMapping("/revenues")
    public List<Revenue> getAllRevenues() {
        log.debug("REST request to get all Revenues");
        return revenueService.findAll();
    }

    /**
     * {@code GET  /revenues/:id} : get the "id" revenue.
     *
     * @param id the id of the revenue to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the revenue, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/revenues/{id}")
    public ResponseEntity<Revenue> getRevenue(@PathVariable Long id) {
        log.debug("REST request to get Revenue : {}", id);
        Optional<Revenue> revenue = revenueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(revenue);
    }

    /**
     * {@code DELETE  /revenues/:id} : delete the "id" revenue.
     *
     * @param id the id of the revenue to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/revenues/{id}")
    public ResponseEntity<Void> deleteRevenue(@PathVariable Long id) {
        log.debug("REST request to delete Revenue : {}", id);
        revenueService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/revenues?query=:query} : search for the revenue corresponding
     * to the query.
     *
     * @param query the query of the revenue search.
     * @return the result of the search.
     */
    @GetMapping("/_search/revenues")
    public List<Revenue> searchRevenues(@RequestParam String query) {
        log.debug("REST request to search Revenues for query {}", query);
        return revenueService.search(query);
    }
}
