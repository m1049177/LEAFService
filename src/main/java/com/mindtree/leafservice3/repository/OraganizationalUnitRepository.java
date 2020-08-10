package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.OraganizationalUnit;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the OraganizationalUnit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OraganizationalUnitRepository extends JpaRepository<OraganizationalUnit, Long> {

}
