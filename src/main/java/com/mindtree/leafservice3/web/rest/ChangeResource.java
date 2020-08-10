package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.Change;
import com.mindtree.leafservice3.service.ChangeService;
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
 * REST controller for managing {@link com.mindtree.leafservice3.domain.Change}.
 */
@RestController
@RequestMapping("/api")
public class ChangeResource {

    private final Logger log = LoggerFactory.getLogger(ChangeResource.class);

    private static final String ENTITY_NAME = "change";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChangeService changeService;

    public ChangeResource(ChangeService changeService) {
        this.changeService = changeService;
    }

    /**
     * {@code POST  /changes} : Create a new change.
     *
     * @param change the change to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new change, or with status {@code 400 (Bad Request)} if the change has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/changes")
    public ResponseEntity<Change> createChange(@Valid @RequestBody Change change) throws URISyntaxException {
        log.debug("REST request to save Change : {}", change);
        if (change.getId() != null) {
            throw new BadRequestAlertException("A new change cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Change result = changeService.save(change);
        return ResponseEntity.created(new URI("/api/changes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /changes} : Updates an existing change.
     *
     * @param change the change to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated change,
     * or with status {@code 400 (Bad Request)} if the change is not valid,
     * or with status {@code 500 (Internal Server Error)} if the change couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/changes")
    public ResponseEntity<Change> updateChange(@Valid @RequestBody Change change) throws URISyntaxException {
        log.debug("REST request to update Change : {}", change);
        if (change.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Change result = changeService.save(change);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, change.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /changes} : get all the changes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of changes in body.
     */
    @GetMapping("/changes")
    public List<Change> getAllChanges() {
        log.debug("REST request to get all Changes");
        return changeService.findAll();
    }

    /**
     * {@code GET  /changes/:id} : get the "id" change.
     *
     * @param id the id of the change to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the change, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/changes/{id}")
    public ResponseEntity<Change> getChange(@PathVariable Long id) {
        log.debug("REST request to get Change : {}", id);
        Optional<Change> change = changeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(change);
    }

    /**
     * {@code DELETE  /changes/:id} : delete the "id" change.
     *
     * @param id the id of the change to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/changes/{id}")
    public ResponseEntity<Void> deleteChange(@PathVariable Long id) {
        log.debug("REST request to delete Change : {}", id);
        changeService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/changes?query=:query} : search for the change corresponding
     * to the query.
     *
     * @param query the query of the change search.
     * @return the result of the search.
     */
    @GetMapping("/_search/changes")
    public List<Change> searchChanges(@RequestParam String query) {
        log.debug("REST request to search Changes for query {}", query);
        return changeService.search(query);
    }

}
