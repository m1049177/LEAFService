package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.Capabilities;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Capabilities entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CapabilitiesRepository extends JpaRepository<Capabilities, Long> {

}
