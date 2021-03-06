package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.Example;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Example entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExampleRepository extends JpaRepository<Example, Long> {

}
