package com.mindtree.leafservice3.repository;

import com.mindtree.leafservice3.domain.Issue;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Issue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

}
