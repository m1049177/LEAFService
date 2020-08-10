package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.IssueService;
import com.mindtree.leafservice3.domain.Issue;
import com.mindtree.leafservice3.repository.IssueRepository;
import com.mindtree.leafservice3.repository.search.IssueSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Issue}.
 */
@Service
@Transactional
public class IssueServiceImpl implements IssueService {

    private final Logger log = LoggerFactory.getLogger(IssueServiceImpl.class);

    private final IssueRepository issueRepository;

    private final IssueSearchRepository issueSearchRepository;

    public IssueServiceImpl(IssueRepository issueRepository, IssueSearchRepository issueSearchRepository) {
        this.issueRepository = issueRepository;
        this.issueSearchRepository = issueSearchRepository;
    }

    /**
     * Save a issue.
     *
     * @param issue the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Issue save(Issue issue) {
        log.debug("Request to save Issue : {}", issue);
        Issue result = issueRepository.save(issue);
        issueSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the issues.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Issue> findAll() {
        log.debug("Request to get all Issues");
        return issueRepository.findAll();
    }


    /**
     * Get one issue by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Issue> findOne(Long id) {
        log.debug("Request to get Issue : {}", id);
        return issueRepository.findById(id);
    }

    /**
     * Delete the issue by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Issue : {}", id);
        issueRepository.deleteById(id);
        issueSearchRepository.deleteById(id);
    }

    /**
     * Search for the issue corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Issue> search(String query) {
        log.debug("Request to search Issues for query {}", query);
        return StreamSupport
            .stream(issueSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
