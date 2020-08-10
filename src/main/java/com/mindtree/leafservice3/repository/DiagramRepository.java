package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.Diagram;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Diagram entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DiagramRepository extends JpaRepository<Diagram, Long> {

}
