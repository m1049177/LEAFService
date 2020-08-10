package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.Spend;
import com.mindtree.leafservice3.service.SpendService;
import com.mindtree.leafservice3.service.dto.ApplicationData;
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
 * REST controller for managing {@link com.mindtree.leafservice3.domain.Spend}.
 */
@RestController
@RequestMapping("/api")
public class SpendResource {

    private final Logger log = LoggerFactory.getLogger(SpendResource.class);

    private static final String ENTITY_NAME = "spend";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SpendService spendService;

    public SpendResource(SpendService spendService) {
        this.spendService = spendService;
    }

    /**
     * {@code POST  /spends} : Create a new spend.
     *
     * @param spend the spend to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new spend, or with status {@code 400 (Bad Request)} if the spend has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/spends")
    public ResponseEntity<Spend> createSpend(@Valid @RequestBody Spend spend) throws URISyntaxException {
        log.debug("REST request to save Spend : {}", spend);
        if (spend.getId() != null) {
            throw new BadRequestAlertException("A new spend cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Spend result = spendService.save(spend);
        return ResponseEntity.created(new URI("/api/spends/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /spends} : Updates an existing spend.
     *
     * @param spend the spend to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated spend,
     * or with status {@code 400 (Bad Request)} if the spend is not valid,
     * or with status {@code 500 (Internal Server Error)} if the spend couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/spends")
    public ResponseEntity<Spend> updateSpend(@Valid @RequestBody Spend spend) throws URISyntaxException {
        log.debug("REST request to update Spend : {}", spend);
        if (spend.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Spend result = spendService.save(spend);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, spend.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /spends} : get all the spends.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of spends in body.
     */
    @GetMapping("/spends")
    public List<Spend> getAllSpends() {
        log.debug("REST request to get all Spends");
        return spendService.findAll();
    }

    @GetMapping("/spends/getSpendData/{company_id}")
    public List<Spend> getSpendData(@PathVariable Long company_id) {
        log.debug("REST request to get all Spends based on company_id");
        return spendService.getSpendData(company_id);
    }

    @GetMapping("/yearlySpendDetails/{company_id}")
    public List<Object> yearlySpendDetails(@PathVariable Long company_id) {
        log.debug("REST request to get yearly spend details");
        log.debug("*********************************************************************************************************"
        +"********************************************************************************");
        return spendService.findYearlySpendDetails(company_id);
    }
    /**
     * {@code GET  /spends/:id} : get the "id" spend.
     *
     * @param id the id of the spend to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the spend, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/spends/{id}")
    public ResponseEntity<Spend> getSpend(@PathVariable Long id) {
        log.debug("REST request to get Spend : {}", id);
        Optional<Spend> spend = spendService.findOne(id);
        return ResponseUtil.wrapOrNotFound(spend);
    }

    /**
     * {@code DELETE  /spends/:id} : delete the "id" spend.
     *
     * @param id the id of the spend to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/spends/{id}")
    public ResponseEntity<Void> deleteSpend(@PathVariable Long id) {
        log.debug("REST request to delete Spend : {}", id);
        spendService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    
    @GetMapping("/spends/getApplicationData/{company_id}")
    public List<ApplicationData> getApplicationData(@PathVariable Long company_id) {
        log.debug("REST request to get allication spend data : {}", company_id);
        return spendService.getApplicationData(company_id);
    }
    /**
     * {@code SEARCH  /_search/spends?query=:query} : search for the spend corresponding
     * to the query.
     *
     * @param query the query of the spend search.
     * @return the result of the search.
     */
    @GetMapping("/_search/spends")
    public List<Spend> searchSpends(@RequestParam String query) {
        log.debug("REST request to search Spends for query {}", query);
        return spendService.search(query);
    }


}
