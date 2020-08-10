package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.Maintenance;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Maintenance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

}
