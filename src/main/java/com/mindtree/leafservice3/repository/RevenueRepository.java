package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.Revenue;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Revenue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long> {

}
