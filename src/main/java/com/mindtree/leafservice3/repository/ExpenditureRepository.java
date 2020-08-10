package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.Expenditure;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Expenditure entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

}
