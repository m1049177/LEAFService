package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.Expenditure;
import com.mindtree.leafservice3.service.ExpenditureService;
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
 * REST controller for managing {@link com.mindtree.leafservice3.domain.Expenditure}.
 */
@RestController
@RequestMapping("/api")
public class ExpenditureResource {

    private final Logger log = LoggerFactory.getLogger(ExpenditureResource.class);

    private static final String ENTITY_NAME = "expenditure";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExpenditureService expenditureService;

    public ExpenditureResource(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    /**
     * {@code POST  /expenditures} : Create a new expenditure.
     *
     * @param expenditure the expenditure to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new expenditure, or with status {@code 400 (Bad Request)} if the expenditure has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/expenditures")
    public ResponseEntity<Expenditure> createExpenditure(@Valid @RequestBody Expenditure expenditure) throws URISyntaxException {
        log.debug("REST request to save Expenditure : {}", expenditure);
        if (expenditure.getId() != null) {
            throw new BadRequestAlertException("A new expenditure cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Expenditure result = expenditureService.save(expenditure);
        return ResponseEntity.created(new URI("/api/expenditures/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /expenditures} : Updates an existing expenditure.
     *
     * @param expenditure the expenditure to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated expenditure,
     * or with status {@code 400 (Bad Request)} if the expenditure is not valid,
     * or with status {@code 500 (Internal Server Error)} if the expenditure couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/expenditures")
    public ResponseEntity<Expenditure> updateExpenditure(@Valid @RequestBody Expenditure expenditure) throws URISyntaxException {
        log.debug("REST request to update Expenditure : {}", expenditure);
        if (expenditure.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Expenditure result = expenditureService.save(expenditure);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, expenditure.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /expenditures} : get all the expenditures.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of expenditures in body.
     */
    @GetMapping("/expenditures")
    public List<Expenditure> getAllExpenditures() {
        log.debug("REST request to get all Expenditures");
        return expenditureService.findAll();
    }

    /**
     * {@code GET  /expenditures/:id} : get the "id" expenditure.
     *
     * @param id the id of the expenditure to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the expenditure, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/expenditures/{id}")
    public ResponseEntity<Expenditure> getExpenditure(@PathVariable Long id) {
        log.debug("REST request to get Expenditure : {}", id);
        Optional<Expenditure> expenditure = expenditureService.findOne(id);
        return ResponseUtil.wrapOrNotFound(expenditure);
    }

    /**
     * {@code DELETE  /expenditures/:id} : delete the "id" expenditure.
     *
     * @param id the id of the expenditure to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/expenditures/{id}")
    public ResponseEntity<Void> deleteExpenditure(@PathVariable Long id) {
        log.debug("REST request to delete Expenditure : {}", id);
        expenditureService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/expenditures?query=:query} : search for the expenditure corresponding
     * to the query.
     *
     * @param query the query of the expenditure search.
     * @return the result of the search.
     */
    @GetMapping("/_search/expenditures")
    public List<Expenditure> searchExpenditures(@RequestParam String query) {
        log.debug("REST request to search Expenditures for query {}", query);
        return expenditureService.search(query);
    }

}
