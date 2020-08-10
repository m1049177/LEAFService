package com.mindtree.leafservice3.repository;
import com.mindtree.leafservice3.domain.TechnologySuggestions;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the TechnologySuggestions entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TechnologySuggestionsRepository extends JpaRepository<TechnologySuggestions, Long> {

}
