package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.Company;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Company entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("select company from Company company where company.user.login = ?#{principal.username}")
    List<Company> findByUserIsCurrentUser();

}
