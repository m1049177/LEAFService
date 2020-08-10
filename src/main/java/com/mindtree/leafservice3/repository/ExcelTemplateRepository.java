package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.ExcelTemplate;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ExcelTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExcelTemplateRepository extends JpaRepository<ExcelTemplate, Long> {

}
