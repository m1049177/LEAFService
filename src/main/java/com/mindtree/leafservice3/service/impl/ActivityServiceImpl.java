package com.mindtree.leafservice3.service.impl;

import com.mindtree.leafservice3.service.ActivityService;
import com.mindtree.leafservice3.domain.Activity;
import com.mindtree.leafservice3.repository.ActivityRepository;
import com.mindtree.leafservice3.repository.search.ActivitySearchRepository;
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
 * Service Implementation for managing {@link Activity}.
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    private final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

    private final ActivityRepository activityRepository;

    private final ActivitySearchRepository activitySearchRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository, ActivitySearchRepository activitySearchRepository) {
        this.activityRepository = activityRepository;
        this.activitySearchRepository = activitySearchRepository;
    }

    /**
     * Save a activity.
     *
     * @param activity the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Activity save(Activity activity) {
        log.debug("Request to save Activity : {}", activity);
        Activity result = activityRepository.save(activity);
        activitySearchRepository.save(result);
        return result;
    }

    /**
     * Get all the activities.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Activity> findAll() {
        log.debug("Request to get all Activities");
        return activityRepository.findAll();
    }


    /**
     * Get one activity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Activity> findOne(Long id) {
        log.debug("Request to get Activity : {}", id);
        return activityRepository.findById(id);
    }

    /**
     * Delete the activity by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Activity : {}", id);
        activityRepository.deleteById(id);
        activitySearchRepository.deleteById(id);
    }

    /**
     * Search for the activity corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Activity> search(String query) {
        log.debug("Request to search Activities for query {}", query);
        return StreamSupport
            .stream(activitySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
