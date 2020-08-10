package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.TechnologyStack;
import com.mindtree.leafservice3.service.TechnologyStackService;
import com.mindtree.leafservice3.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.mindtree.leafservice3.domain.TechnologyStack}.
 */
@RestController
@RequestMapping("/api")
public class TechnologyStackResource {

    private final Logger log = LoggerFactory.getLogger(TechnologyStackResource.class);

    private static final String ENTITY_NAME = "technologyStack";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TechnologyStackService technologyStackService;

    public TechnologyStackResource(TechnologyStackService technologyStackService) {
        this.technologyStackService = technologyStackService;
    }

    /**
     * {@code POST  /technology-stacks} : Create a new technologyStack.
     *
     * @param technologyStack the technologyStack to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new technologyStack, or with status {@code 400 (Bad Request)} if the technologyStack has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/technology-stacks")
    public ResponseEntity<TechnologyStack> createTechnologyStack(@RequestBody TechnologyStack technologyStack) throws URISyntaxException {
        log.debug("REST request to save TechnologyStack : {}", technologyStack);
        if (technologyStack.getId() != null) {
            throw new BadRequestAlertException("A new technologyStack cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TechnologyStack result = technologyStackService.save(technologyStack);
        return ResponseEntity.created(new URI("/api/technology-stacks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /technology-stacks} : Updates an existing technologyStack.
     *
     * @param technologyStack the technologyStack to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated technologyStack,
     * or with status {@code 400 (Bad Request)} if the technologyStack is not valid,
     * or with status {@code 500 (Internal Server Error)} if the technologyStack couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/technology-stacks")
    public ResponseEntity<TechnologyStack> updateTechnologyStack(@RequestBody TechnologyStack technologyStack) throws URISyntaxException {
        log.debug("REST request to update TechnologyStack : {}", technologyStack);
        if (technologyStack.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TechnologyStack result = technologyStackService.save(technologyStack);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, technologyStack.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /technology-stacks} : get all the technologyStacks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of technologyStacks in body.
     */
    @GetMapping("/technology-stacks")
    public List<TechnologyStack> getAllTechnologyStacks() {
        log.debug("REST request to get all TechnologyStacks");
        return technologyStackService.findAll();
    }

    /**
     * {@code GET  /technology-stacks/:id} : get the "id" technologyStack.
     *
     * @param id the id of the technologyStack to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the technologyStack, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/technology-stacks/{id}")
    public ResponseEntity<TechnologyStack> getTechnologyStack(@PathVariable Long id) {
        log.debug("REST request to get TechnologyStack : {}", id);
        Optional<TechnologyStack> technologyStack = technologyStackService.findOne(id);
        return ResponseUtil.wrapOrNotFound(technologyStack);
    }

    /**
     * {@code DELETE  /technology-stacks/:id} : delete the "id" technologyStack.
     *
     * @param id the id of the technologyStack to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/technology-stacks/{id}")
    public ResponseEntity<Void> deleteTechnologyStack(@PathVariable Long id) {
        log.debug("REST request to delete TechnologyStack : {}", id);
        technologyStackService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/technology-stacks?query=:query} : search for the technologyStack corresponding
     * to the query.
     *
     * @param query the query of the technologyStack search.
     * @return the result of the search.
     */
    @GetMapping("/_search/technology-stacks")
    public List<TechnologyStack> searchTechnologyStacks(@RequestParam String query) {
        log.debug("REST request to search TechnologyStacks for query {}", query);
        return technologyStackService.search(query);
    }

}
