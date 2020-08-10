package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.TechnologySuggestions;
import com.mindtree.leafservice3.repository.TechnologySuggestionsRepository;
import com.mindtree.leafservice3.repository.search.TechnologySuggestionsSearchRepository;
import com.mindtree.leafservice3.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.mindtree.leafservice3.domain.TechnologySuggestions}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TechnologySuggestionsResource {

    private final Logger log = LoggerFactory.getLogger(TechnologySuggestionsResource.class);

    private static final String ENTITY_NAME = "technologySuggestions";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TechnologySuggestionsRepository technologySuggestionsRepository;

    private final TechnologySuggestionsSearchRepository technologySuggestionsSearchRepository;

    public TechnologySuggestionsResource(TechnologySuggestionsRepository technologySuggestionsRepository, TechnologySuggestionsSearchRepository technologySuggestionsSearchRepository) {
        this.technologySuggestionsRepository = technologySuggestionsRepository;
        this.technologySuggestionsSearchRepository = technologySuggestionsSearchRepository;
    }

    /**
     * {@code POST  /technology-suggestions} : Create a new technologySuggestions.
     *
     * @param technologySuggestions the technologySuggestions to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new technologySuggestions, or with status {@code 400 (Bad Request)} if the technologySuggestions has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/technology-suggestions")
    public ResponseEntity<TechnologySuggestions> createTechnologySuggestions(@RequestBody TechnologySuggestions technologySuggestions) throws URISyntaxException {
        log.debug("REST request to save TechnologySuggestions : {}", technologySuggestions);
        if (technologySuggestions.getId() != null) {
            throw new BadRequestAlertException("A new technologySuggestions cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TechnologySuggestions result = technologySuggestionsRepository.save(technologySuggestions);
        System.out.println("****************************");
        System.out.println(result);
        technologySuggestionsSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/technology-suggestions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /technology-suggestions} : Updates an existing technologySuggestions.
     *
     * @param technologySuggestions the technologySuggestions to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated technologySuggestions,
     * or with status {@code 400 (Bad Request)} if the technologySuggestions is not valid,
     * or with status {@code 500 (Internal Server Error)} if the technologySuggestions couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/technology-suggestions")
    public ResponseEntity<TechnologySuggestions> updateTechnologySuggestions(@RequestBody TechnologySuggestions technologySuggestions) throws URISyntaxException {
        log.debug("REST request to update TechnologySuggestions : {}", technologySuggestions);
        if (technologySuggestions.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TechnologySuggestions result = technologySuggestionsRepository.save(technologySuggestions);
        technologySuggestionsSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, technologySuggestions.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /technology-suggestions} : get all the technologySuggestions.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of technologySuggestions in body.
     */
    @GetMapping("/technology-suggestions")
    public List<TechnologySuggestions> getAllTechnologySuggestions() {
        log.debug("REST request to get all TechnologySuggestions");
        return technologySuggestionsRepository.findAll();
    }

    /**
     * {@code GET  /technology-suggestions/:id} : get the "id" technologySuggestions.
     *
     * @param id the id of the technologySuggestions to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the technologySuggestions, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/technology-suggestions/{id}")
    public ResponseEntity<TechnologySuggestions> getTechnologySuggestions(@PathVariable Long id) {
        log.debug("REST request to get TechnologySuggestions : {}", id);
        Optional<TechnologySuggestions> technologySuggestions = technologySuggestionsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(technologySuggestions);
    }

    /**
     * {@code DELETE  /technology-suggestions/:id} : delete the "id" technologySuggestions.
     *
     * @param id the id of the technologySuggestions to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/technology-suggestions/{id}")
    public ResponseEntity<Void> deleteTechnologySuggestions(@PathVariable Long id) {
        log.debug("REST request to delete TechnologySuggestions : {}", id);
        technologySuggestionsRepository.deleteById(id);
        technologySuggestionsSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/technology-suggestions?query=:query} : search for the technologySuggestions corresponding
     * to the query.
     *
     * @param query the query of the technologySuggestions search.
     * @return the result of the search.
     */
    @GetMapping("/_search/technology-suggestions")
    public List<TechnologySuggestions> searchTechnologySuggestions(@RequestParam String query) {
        log.debug("REST request to search TechnologySuggestions for query {}", query);
        return StreamSupport
            .stream(technologySuggestionsSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
