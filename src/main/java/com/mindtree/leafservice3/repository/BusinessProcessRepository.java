package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.BusinessProcess;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the BusinessProcess entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BusinessProcessRepository extends JpaRepository<BusinessProcess, Long> {

}
