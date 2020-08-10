package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.Evaluation;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Evaluation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

}
