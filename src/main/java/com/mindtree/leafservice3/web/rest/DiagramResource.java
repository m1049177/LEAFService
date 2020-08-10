package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.Diagram;
import com.mindtree.leafservice3.repository.DiagramRepository;
import com.mindtree.leafservice3.repository.search.DiagramSearchRepository;
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
 * REST controller for managing {@link com.mindtree.leafservice3.domain.Diagram}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class DiagramResource {

    private final Logger log = LoggerFactory.getLogger(DiagramResource.class);

    private static final String ENTITY_NAME = "diagram";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DiagramRepository diagramRepository;

    private final DiagramSearchRepository diagramSearchRepository;

    public DiagramResource(DiagramRepository diagramRepository, DiagramSearchRepository diagramSearchRepository) {
        this.diagramRepository = diagramRepository;
        this.diagramSearchRepository = diagramSearchRepository;
    }

    /**
     * {@code POST  /diagrams} : Create a new diagram.
     *
     * @param diagram the diagram to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new diagram, or with status {@code 400 (Bad Request)} if the diagram has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/diagrams")
    public ResponseEntity<Diagram> createDiagram(@Valid @RequestBody Diagram diagram) throws URISyntaxException {
        log.debug("REST request to save Diagram : {}", diagram);
        if (diagram.getId() != null) {
            throw new BadRequestAlertException("A new diagram cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Diagram result = diagramRepository.save(diagram);
        diagramSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/diagrams/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /diagrams} : Updates an existing diagram.
     *
     * @param diagram the diagram to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated diagram,
     * or with status {@code 400 (Bad Request)} if the diagram is not valid,
     * or with status {@code 500 (Internal Server Error)} if the diagram couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/diagrams")
    public ResponseEntity<Diagram> updateDiagram(@Valid @RequestBody Diagram diagram) throws URISyntaxException {
        log.debug("REST request to update Diagram : {}", diagram);
        if (diagram.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Diagram result = diagramRepository.save(diagram);
        diagramSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, diagram.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /diagrams} : get all the diagrams.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of diagrams in body.
     */
    @GetMapping("/diagrams")
    public List<Diagram> getAllDiagrams() {
        log.debug("REST request to get all Diagrams");
        return diagramRepository.findAll();
    }

    /**
     * {@code GET  /diagrams/:id} : get the "id" diagram.
     *
     * @param id the id of the diagram to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the diagram, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/diagrams/{id}")
    public ResponseEntity<Diagram> getDiagram(@PathVariable Long id) {
        log.debug("REST request to get Diagram : {}", id);
        Optional<Diagram> diagram = diagramRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(diagram);
    }

    /**
     * {@code DELETE  /diagrams/:id} : delete the "id" diagram.
     *
     * @param id the id of the diagram to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/diagrams/{id}")
    public ResponseEntity<Void> deleteDiagram(@PathVariable Long id) {
        log.debug("REST request to delete Diagram : {}", id);
        diagramRepository.deleteById(id);
        diagramSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/diagrams?query=:query} : search for the diagram corresponding
     * to the query.
     *
     * @param query the query of the diagram search.
     * @return the result of the search.
     */
    @GetMapping("/_search/diagrams")
    public List<Diagram> searchDiagrams(@RequestParam String query) {
        log.debug("REST request to search Diagrams for query {}", query);
        return StreamSupport
            .stream(diagramSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
