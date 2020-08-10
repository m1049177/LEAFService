package com.mindtree.leafservice3.web.rest;

import com.mindtree.leafservice3.domain.LineOfBusiness;
import com.mindtree.leafservice3.service.LineOfBusinessService;
import com.mindtree.leafservice3.service.dto.LobSearchData;
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
 * REST controller for managing {@link com.mindtree.leafservice3.domain.LineOfBusiness}.
 */
@RestController
@RequestMapping("/api")
public class LineOfBusinessResource {

    private final Logger log = LoggerFactory.getLogger(LineOfBusinessResource.class);

    private static final String ENTITY_NAME = "lineOfBusiness";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LineOfBusinessService lineOfBusinessService;

    public LineOfBusinessResource(LineOfBusinessService lineOfBusinessService) {
        this.lineOfBusinessService = lineOfBusinessService;
    }

    /**
     * {@code POST  /line-of-businesses} : Create a new lineOfBusiness.
     *
     * @param lineOfBusiness the lineOfBusiness to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lineOfBusiness, or with status {@code 400 (Bad Request)} if the lineOfBusiness has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/line-of-businesses")
    public ResponseEntity<LineOfBusiness> createLineOfBusiness(@Valid @RequestBody LineOfBusiness lineOfBusiness) throws URISyntaxException {
        log.debug("REST request to save LineOfBusiness : {}", lineOfBusiness);
        if (lineOfBusiness.getId() != null) {
            throw new BadRequestAlertException("A new lineOfBusiness cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LineOfBusiness result = lineOfBusinessService.save(lineOfBusiness);
        return ResponseEntity.created(new URI("/api/line-of-businesses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /line-of-businesses} : Updates an existing lineOfBusiness.
     *
     * @param lineOfBusiness the lineOfBusiness to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lineOfBusiness,
     * or with status {@code 400 (Bad Request)} if the lineOfBusiness is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lineOfBusiness couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/line-of-businesses")
    public ResponseEntity<LineOfBusiness> updateLineOfBusiness(@Valid @RequestBody LineOfBusiness lineOfBusiness) throws URISyntaxException {
        log.debug("REST request to update LineOfBusiness : {}", lineOfBusiness);
        if (lineOfBusiness.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LineOfBusiness result = lineOfBusinessService.save(lineOfBusiness);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, lineOfBusiness.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /line-of-businesses} : get all the lineOfBusinesses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lineOfBusinesses in body.
     */
    @GetMapping("/line-of-businesses")
    public List<LineOfBusiness> getAllLineOfBusinesses() {
        log.debug("REST request to get all LineOfBusinesses");
        return lineOfBusinessService.findAll();
    }

    /**
     * {@code GET  /line-of-businesses/:id} : get the "id" lineOfBusiness.
     *
     * @param id the id of the lineOfBusiness to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lineOfBusiness, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/line-of-businesses/{id}")
    public ResponseEntity<LineOfBusiness> getLineOfBusiness(@PathVariable Long id) {
        log.debug("REST request to get LineOfBusiness : {}", id);
        Optional<LineOfBusiness> lineOfBusiness = lineOfBusinessService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lineOfBusiness);
    }

    /**
     * {@code DELETE  /line-of-businesses/:id} : delete the "id" lineOfBusiness.
     *
     * @param id the id of the lineOfBusiness to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/line-of-businesses/{id}")
    public ResponseEntity<Void> deleteLineOfBusiness(@PathVariable Long id) {
        log.debug("REST request to delete LineOfBusiness : {}", id);
        lineOfBusinessService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/line-of-businesses?query=:query} : search for the lineOfBusiness corresponding
     * to the query.
     *
     * @param query the query of the lineOfBusiness search.
     * @return the result of the search.
     */
    @GetMapping("/_search/line-of-businesses")
    public List<LineOfBusiness> searchLineOfBusinesses(@RequestParam String query) {
        log.debug("REST request to search LineOfBusinesses for query {}", query);
        return lineOfBusinessService.search(query);
    }

    @GetMapping("/_search/lob/{lob_id}")
    public LobSearchData searchData(@PathVariable Long lob_id) {
        log.debug("REST request to search LineOfBusinesses for query {}", lob_id);
        return lineOfBusinessService.searchData(lob_id);
    }
}
