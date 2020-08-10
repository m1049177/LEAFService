package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.Functionality;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Functionality entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FunctionalityRepository extends JpaRepository<Functionality, Long> {

}
