package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.Evaluation;
import com.mindtree.leafservice3.repository.EvaluationRepository;
import com.mindtree.leafservice3.repository.search.EvaluationSearchRepository;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.mindtree.leafservice3.domain.Evaluation}.
 */
@RestController
@RequestMapping("/api")
public class EvaluationResource {

    private final Logger log = LoggerFactory.getLogger(EvaluationResource.class);

    private static final String ENTITY_NAME = "evaluation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EvaluationRepository evaluationRepository;

    private final EvaluationSearchRepository evaluationSearchRepository;

    public EvaluationResource(EvaluationRepository evaluationRepository, EvaluationSearchRepository evaluationSearchRepository) {
        this.evaluationRepository = evaluationRepository;
        this.evaluationSearchRepository = evaluationSearchRepository;
    }

    /**
     * {@code POST  /evaluations} : Create a new evaluation.
     *
     * @param evaluation the evaluation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new evaluation, or with status {@code 400 (Bad Request)} if the evaluation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/evaluations")
    public ResponseEntity<Evaluation> createEvaluation(@Valid @RequestBody Evaluation evaluation) throws URISyntaxException {
        log.debug("REST request to save Evaluation : {}", evaluation);
        if (evaluation.getId() != null) {
            throw new BadRequestAlertException("A new evaluation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        System.out.println("Check"+evaluation);
        Evaluation result = evaluationRepository.save(evaluation);
        evaluationSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/evaluations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /evaluations} : Updates an existing evaluation.
     *
     * @param evaluation the evaluation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated evaluation,
     * or with status {@code 400 (Bad Request)} if the evaluation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the evaluation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/evaluations")
    public ResponseEntity<Evaluation> updateEvaluation(@Valid @RequestBody Evaluation evaluation) throws URISyntaxException {
        log.debug("REST request to update Evaluation : {}", evaluation);
        if (evaluation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Evaluation result = evaluationRepository.save(evaluation);
        evaluationSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, evaluation.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /evaluations} : get all the evaluations.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of evaluations in body.
     */
    @GetMapping("/evaluations")
    public List<Evaluation> getAllEvaluations() {
        log.debug("REST request to get all Evaluations");
        return evaluationRepository.findAll();
    }

    /**
     * {@code GET  /evaluations/:id} : get the "id" evaluation.
     *
     * @param id the id of the evaluation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the evaluation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/evaluations/{id}")
    public ResponseEntity<Evaluation> getEvaluation(@PathVariable Long id) {
        log.debug("REST request to get Evaluation : {}", id);
        Optional<Evaluation> evaluation = evaluationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(evaluation);
    }

    /**
     * {@code DELETE  /evaluations/:id} : delete the "id" evaluation.
     *
     * @param id the id of the evaluation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/evaluations/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
        log.debug("REST request to delete Evaluation : {}", id);
        evaluationRepository.deleteById(id);
        evaluationSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/evaluations?query=:query} : search for the evaluation corresponding
     * to the query.
     *
     * @param query the query of the evaluation search.
     * @return the result of the search.
     */
    @GetMapping("/_search/evaluations")
    public List<Evaluation> searchEvaluations(@RequestParam String query) {
        log.debug("REST request to search Evaluations for query {}", query);
        return StreamSupport
            .stream(evaluationSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
