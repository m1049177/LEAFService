package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.Change;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Change entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChangeRepository extends JpaRepository<Change, Long> {

}
