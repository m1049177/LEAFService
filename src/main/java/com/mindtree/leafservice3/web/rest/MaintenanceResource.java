package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.Maintenance;
import com.mindtree.leafservice3.service.MaintenanceService;
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
 * REST controller for managing {@link com.mindtree.leafservice3.domain.Maintenance}.
 */
@RestController
@RequestMapping("/api")
public class MaintenanceResource {

    private final Logger log = LoggerFactory.getLogger(MaintenanceResource.class);

    private static final String ENTITY_NAME = "maintenance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MaintenanceService maintenanceService;

    public MaintenanceResource(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    /**
     * {@code POST  /maintenances} : Create a new maintenance.
     *
     * @param maintenance the maintenance to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new maintenance, or with status {@code 400 (Bad Request)} if the maintenance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/maintenances")
    public ResponseEntity<Maintenance> createMaintenance(@Valid @RequestBody Maintenance maintenance) throws URISyntaxException {
        log.debug("REST request to save Maintenance : {}", maintenance);
        if (maintenance.getId() != null) {
            throw new BadRequestAlertException("A new maintenance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Maintenance result = maintenanceService.save(maintenance);
        return ResponseEntity.created(new URI("/api/maintenances/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /maintenances} : Updates an existing maintenance.
     *
     * @param maintenance the maintenance to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maintenance,
     * or with status {@code 400 (Bad Request)} if the maintenance is not valid,
     * or with status {@code 500 (Internal Server Error)} if the maintenance couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/maintenances")
    public ResponseEntity<Maintenance> updateMaintenance(@Valid @RequestBody Maintenance maintenance) throws URISyntaxException {
        log.debug("REST request to update Maintenance : {}", maintenance);
        if (maintenance.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Maintenance result = maintenanceService.save(maintenance);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, maintenance.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /maintenances} : get all the maintenances.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of maintenances in body.
     */
    @GetMapping("/maintenances")
    public List<Maintenance> getAllMaintenances() {
        log.debug("REST request to get all Maintenances");
        return maintenanceService.findAll();
    }

    /**
     * {@code GET  /maintenances/:id} : get the "id" maintenance.
     *
     * @param id the id of the maintenance to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the maintenance, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/maintenances/{id}")
    public ResponseEntity<Maintenance> getMaintenance(@PathVariable Long id) {
        log.debug("REST request to get Maintenance : {}", id);
        Optional<Maintenance> maintenance = maintenanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(maintenance);
    }

    /**
     * {@code DELETE  /maintenances/:id} : delete the "id" maintenance.
     *
     * @param id the id of the maintenance to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/maintenances/{id}")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long id) {
        log.debug("REST request to delete Maintenance : {}", id);
        maintenanceService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/maintenances?query=:query} : search for the maintenance corresponding
     * to the query.
     *
     * @param query the query of the maintenance search.
     * @return the result of the search.
     */
    @GetMapping("/_search/maintenances")
    public List<Maintenance> searchMaintenances(@RequestParam String query) {
        log.debug("REST request to search Maintenances for query {}", query);
        return maintenanceService.search(query);
    }

}
