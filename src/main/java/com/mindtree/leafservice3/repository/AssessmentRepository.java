package com.mindtree.leafservice3.repository;
import com.mindtree.leafservice3.domain.Assessment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Assessment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

}
