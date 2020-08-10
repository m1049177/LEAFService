package com.mindtree.leafservice3.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.mindtree.leafservice3.web.rest.TestUtil;

public class RevenueTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Revenue.class);
        Revenue revenue1 = new Revenue();
        revenue1.setId(1L);
        Revenue revenue2 = new Revenue();
        revenue2.setId(revenue1.getId());
        assertThat(revenue1).isEqualTo(revenue2);
        revenue2.setId(2L);
        assertThat(revenue1).isNotEqualTo(revenue2);
        revenue1.setId(null);
        assertThat(revenue1).isNotEqualTo(revenue2);
    }
}
