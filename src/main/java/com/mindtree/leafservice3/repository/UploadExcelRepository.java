package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.UploadExcel;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the UploadExcel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UploadExcelRepository extends JpaRepository<UploadExcel, Long> {

}
