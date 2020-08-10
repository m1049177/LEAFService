package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.TechnologyRecommendation;
import com.mindtree.leafservice3.repository.TechnologyRecommendationRepository;
import com.mindtree.leafservice3.repository.search.TechnologyRecommendationSearchRepository;
import com.mindtree.leafservice3.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; 
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
 * REST controller for managing {@link com.mindtree.leafservice3.domain.TechnologyRecommendation}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TechnologyRecommendationResource {

    private final Logger log = LoggerFactory.getLogger(TechnologyRecommendationResource.class);

    private static final String ENTITY_NAME = "technologyRecommendation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TechnologyRecommendationRepository technologyRecommendationRepository;

    private final TechnologyRecommendationSearchRepository technologyRecommendationSearchRepository;

    public TechnologyRecommendationResource(TechnologyRecommendationRepository technologyRecommendationRepository, TechnologyRecommendationSearchRepository technologyRecommendationSearchRepository) {
        this.technologyRecommendationRepository = technologyRecommendationRepository;
        this.technologyRecommendationSearchRepository = technologyRecommendationSearchRepository;
    }

    /**
     * {@code POST  /technology-recommendations} : Create a new technologyRecommendation.
     *
     * @param technologyRecommendation the technologyRecommendation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new technologyRecommendation, or with status {@code 400 (Bad Request)} if the technologyRecommendation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/technology-recommendations")
    public ResponseEntity<TechnologyRecommendation> createTechnologyRecommendation(@Valid @RequestBody TechnologyRecommendation technologyRecommendation) throws URISyntaxException {
        log.debug("REST request to save TechnologyRecommendation : {}", technologyRecommendation);
        if (technologyRecommendation.getId() != null) {
            throw new BadRequestAlertException("A new technologyRecommendation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TechnologyRecommendation result = technologyRecommendationRepository.save(technologyRecommendation);
        technologyRecommendationSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/technology-recommendations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /technology-recommendations} : Updates an existing technologyRecommendation.
     *
     * @param technologyRecommendation the technologyRecommendation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated technologyRecommendation,
     * or with status {@code 400 (Bad Request)} if the technologyRecommendation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the technologyRecommendation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/technology-recommendations")
    public ResponseEntity<TechnologyRecommendation> updateTechnologyRecommendation(@Valid @RequestBody TechnologyRecommendation technologyRecommendation) throws URISyntaxException {
        log.debug("REST request to update TechnologyRecommendation : {}", technologyRecommendation);
        if (technologyRecommendation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TechnologyRecommendation result = technologyRecommendationRepository.save(technologyRecommendation);
        technologyRecommendationSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, technologyRecommendation.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /technology-recommendations} : get all the technologyRecommendations.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of technologyRecommendations in body.
     */
    @GetMapping("/technology-recommendations")
    public List<TechnologyRecommendation> getAllTechnologyRecommendations() {
        log.debug("REST request to get all TechnologyRecommendations");
        return technologyRecommendationRepository.findAll();
    }

    /**
     * {@code GET  /technology-recommendations/:id} : get the "id" technologyRecommendation.
     *
     * @param id the id of the technologyRecommendation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the technologyRecommendation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/technology-recommendations/{id}")
    public ResponseEntity<TechnologyRecommendation> getTechnologyRecommendation(@PathVariable Long id) {
        log.debug("REST request to get TechnologyRecommendation : {}", id);
        Optional<TechnologyRecommendation> technologyRecommendation = technologyRecommendationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(technologyRecommendation);
    }

    /**
     * {@code DELETE  /technology-recommendations/:id} : delete the "id" technologyRecommendation.
     *
     * @param id the id of the technologyRecommendation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/technology-recommendations/{id}")
    public ResponseEntity<Void> deleteTechnologyRecommendation(@PathVariable Long id) {
        log.debug("REST request to delete TechnologyRecommendation : {}", id);
        technologyRecommendationRepository.deleteById(id);
        technologyRecommendationSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/technology-recommendations?query=:query} : search for the technologyRecommendation corresponding
     * to the query.
     *
     * @param query the query of the technologyRecommendation search.
     * @return the result of the search.
     */
    @GetMapping("/_search/technology-recommendations")
    public List<TechnologyRecommendation> searchTechnologyRecommendations(@RequestParam String query) {
        log.debug("REST request to search TechnologyRecommendations for query {}", query);
        return StreamSupport
            .stream(technologyRecommendationSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
