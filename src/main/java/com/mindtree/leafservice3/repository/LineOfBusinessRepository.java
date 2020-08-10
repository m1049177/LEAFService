package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.LineOfBusiness;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the LineOfBusiness entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LineOfBusinessRepository extends JpaRepository<LineOfBusiness, Long> {

}
