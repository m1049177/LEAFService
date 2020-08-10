package com.mindtree.leafservice3.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.mindtree.leafservice3.web.rest.TestUtil;

public class TechnologySuggestionsTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TechnologySuggestions.class);
        TechnologySuggestions technologySuggestions1 = new TechnologySuggestions();
        technologySuggestions1.setId(1L);
        TechnologySuggestions technologySuggestions2 = new TechnologySuggestions();
        technologySuggestions2.setId(technologySuggestions1.getId());
        assertThat(technologySuggestions1).isEqualTo(technologySuggestions2);
        technologySuggestions2.setId(2L);
        assertThat(technologySuggestions1).isNotEqualTo(technologySuggestions2);
        technologySuggestions1.setId(null);
        assertThat(technologySuggestions1).isNotEqualTo(technologySuggestions2);
    }
}
