package com.mindtree.leafservice3.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.mindtree.leafservice3.web.rest.TestUtil;

public class TechnologyRecommendationTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TechnologyRecommendation.class);
        TechnologyRecommendation technologyRecommendation1 = new TechnologyRecommendation();
        technologyRecommendation1.setId(1L);
        TechnologyRecommendation technologyRecommendation2 = new TechnologyRecommendation();
        technologyRecommendation2.setId(technologyRecommendation1.getId());
        assertThat(technologyRecommendation1).isEqualTo(technologyRecommendation2);
        technologyRecommendation2.setId(2L);
        assertThat(technologyRecommendation1).isNotEqualTo(technologyRecommendation2);
        technologyRecommendation1.setId(null);
        assertThat(technologyRecommendation1).isNotEqualTo(technologyRecommendation2);
    }
}
