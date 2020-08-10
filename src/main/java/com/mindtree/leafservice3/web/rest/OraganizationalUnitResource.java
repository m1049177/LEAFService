package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.OraganizationalUnit;
import com.mindtree.leafservice3.service.OraganizationalUnitService;
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
 * REST controller for managing {@link com.mindtree.leafservice3.domain.OraganizationalUnit}.
 */
@RestController
@RequestMapping("/api")
public class OraganizationalUnitResource {

    private final Logger log = LoggerFactory.getLogger(OraganizationalUnitResource.class);

    private static final String ENTITY_NAME = "oraganizationalUnit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OraganizationalUnitService oraganizationalUnitService;

    public OraganizationalUnitResource(OraganizationalUnitService oraganizationalUnitService) {
        this.oraganizationalUnitService = oraganizationalUnitService;
    }

    /**
     * {@code POST  /oraganizational-units} : Create a new oraganizationalUnit.
     *
     * @param oraganizationalUnit the oraganizationalUnit to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new oraganizationalUnit, or with status {@code 400 (Bad Request)} if the oraganizationalUnit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/oraganizational-units")
    public ResponseEntity<OraganizationalUnit> createOraganizationalUnit(@Valid @RequestBody OraganizationalUnit oraganizationalUnit) throws URISyntaxException {
        log.debug("REST request to save OraganizationalUnit : {}", oraganizationalUnit);
        if (oraganizationalUnit.getId() != null) {
            throw new BadRequestAlertException("A new oraganizationalUnit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OraganizationalUnit result = oraganizationalUnitService.save(oraganizationalUnit);
        return ResponseEntity.created(new URI("/api/oraganizational-units/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /oraganizational-units} : Updates an existing oraganizationalUnit.
     *
     * @param oraganizationalUnit the oraganizationalUnit to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated oraganizationalUnit,
     * or with status {@code 400 (Bad Request)} if the oraganizationalUnit is not valid,
     * or with status {@code 500 (Internal Server Error)} if the oraganizationalUnit couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/oraganizational-units")
    public ResponseEntity<OraganizationalUnit> updateOraganizationalUnit(@Valid @RequestBody OraganizationalUnit oraganizationalUnit) throws URISyntaxException {
        log.debug("REST request to update OraganizationalUnit : {}", oraganizationalUnit);
        if (oraganizationalUnit.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        OraganizationalUnit result = oraganizationalUnitService.save(oraganizationalUnit);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, oraganizationalUnit.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /oraganizational-units} : get all the oraganizationalUnits.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of oraganizationalUnits in body.
     */
    @GetMapping("/oraganizational-units")
    public List<OraganizationalUnit> getAllOraganizationalUnits() {
        log.debug("REST request to get all OraganizationalUnits");
        return oraganizationalUnitService.findAll();
    }

    /**
     * {@code GET  /oraganizational-units/:id} : get the "id" oraganizationalUnit.
     *
     * @param id the id of the oraganizationalUnit to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the oraganizationalUnit, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/oraganizational-units/{id}")
    public ResponseEntity<OraganizationalUnit> getOraganizationalUnit(@PathVariable Long id) {
        log.debug("REST request to get OraganizationalUnit : {}", id);
        Optional<OraganizationalUnit> oraganizationalUnit = oraganizationalUnitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(oraganizationalUnit);
    }

    /**
     * {@code DELETE  /oraganizational-units/:id} : delete the "id" oraganizationalUnit.
     *
     * @param id the id of the oraganizationalUnit to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/oraganizational-units/{id}")
    public ResponseEntity<Void> deleteOraganizationalUnit(@PathVariable Long id) {
        log.debug("REST request to delete OraganizationalUnit : {}", id);
        oraganizationalUnitService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/oraganizational-units?query=:query} : search for the oraganizationalUnit corresponding
     * to the query.
     *
     * @param query the query of the oraganizationalUnit search.
     * @return the result of the search.
     */
    @GetMapping("/_search/oraganizational-units")
    public List<OraganizationalUnit> searchOraganizationalUnits(@RequestParam String query) {
        log.debug("REST request to search OraganizationalUnits for query {}", query);
        return oraganizationalUnitService.search(query);
    }

}
