package com.mindtree.leafservice3.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.mindtree.leafservice3.web.rest.TestUtil;

public class SpendTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Spend.class);
        Spend spend1 = new Spend();
        spend1.setId(1L);
        Spend spend2 = new Spend();
        spend2.setId(spend1.getId());
        assertThat(spend1).isEqualTo(spend2);
        spend2.setId(2L);
        assertThat(spend1).isNotEqualTo(spend2);
        spend1.setId(null);
        assertThat(spend1).isNotEqualTo(spend2);
    }
}
