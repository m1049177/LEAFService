package com.mindtree.leafservice3.repository;
import com.mindtree.leafservice3.domain.TechnologyRecommendation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the TechnologyRecommendation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TechnologyRecommendationRepository extends JpaRepository<TechnologyRecommendation, Long> {

}
