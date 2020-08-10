package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.TechnologyStack;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the TechnologyStack entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TechnologyStackRepository extends JpaRepository<TechnologyStack, Long> {

}
