package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.Spend;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Spend entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SpendRepository extends JpaRepository<Spend, Long> {

}
