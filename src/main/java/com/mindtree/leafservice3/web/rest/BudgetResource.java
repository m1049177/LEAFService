package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.Budget;
import com.mindtree.leafservice3.repository.BudgetRepository;
import com.mindtree.leafservice3.repository.search.BudgetSearchRepository;
import com.mindtree.leafservice3.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.mindtree.leafservice3.domain.Budget}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BudgetResource {

    private final Logger log = LoggerFactory.getLogger(BudgetResource.class);

    private static final String ENTITY_NAME = "budget";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BudgetRepository budgetRepository;

    private final BudgetSearchRepository budgetSearchRepository;

    public BudgetResource(BudgetRepository budgetRepository, BudgetSearchRepository budgetSearchRepository) {
        this.budgetRepository = budgetRepository;
        this.budgetSearchRepository = budgetSearchRepository;
    }

    /**
     * {@code POST  /budgets} : Create a new budget.
     *
     * @param budget the budget to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new budget, or with status {@code 400 (Bad Request)} if the budget has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/budgets")
    public ResponseEntity<Budget> createBudget(@Valid @RequestBody Budget budget) throws URISyntaxException {
        log.debug("REST request to save Budget : {}", budget);
        if (budget.getId() != null) {
            throw new BadRequestAlertException("A new budget cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Budget result = budgetRepository.save(budget);
        budgetSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/budgets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /budgets} : Updates an existing budget.
     *
     * @param budget the budget to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated budget,
     * or with status {@code 400 (Bad Request)} if the budget is not valid,
     * or with status {@code 500 (Internal Server Error)} if the budget couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/budgets")
    public ResponseEntity<Budget> updateBudget(@Valid @RequestBody Budget budget) throws URISyntaxException {
        log.debug("REST request to update Budget : {}", budget);
        if (budget.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Budget result = budgetRepository.save(budget);
        budgetSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, budget.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /budgets} : get all the budgets.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of budgets in body.
     */
    @GetMapping("/budgets")
    public ResponseEntity<List<Budget>> getAllBudgets(Pageable pageable) {
        log.debug("REST request to get a page of Budgets");
        Page<Budget> page = budgetRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /budgets/:id} : get the "id" budget.
     *
     * @param id the id of the budget to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the budget, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/budgets/{id}")
    public ResponseEntity<Budget> getBudget(@PathVariable Long id) {
        log.debug("REST request to get Budget : {}", id);
        Optional<Budget> budget = budgetRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(budget);
    }

    /**
     * {@code DELETE  /budgets/:id} : delete the "id" budget.
     *
     * @param id the id of the budget to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/budgets/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        log.debug("REST request to delete Budget : {}", id);
        budgetRepository.deleteById(id);
        budgetSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/budgets?query=:query} : search for the budget corresponding
     * to the query.
     *
     * @param query the query of the budget search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/budgets")
    public ResponseEntity<List<Budget>> searchBudgets(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Budgets for query {}", query);
        Page<Budget> page = budgetSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
