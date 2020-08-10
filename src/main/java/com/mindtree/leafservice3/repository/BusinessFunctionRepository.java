package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.BusinessFunction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the BusinessFunction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BusinessFunctionRepository extends JpaRepository<BusinessFunction, Long> {

}
