package com.mindtree.leafservice3.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.mindtree.leafservice3.web.rest.TestUtil;

public class DiagramTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Diagram.class);
        Diagram diagram1 = new Diagram();
        diagram1.setId(1L);
        Diagram diagram2 = new Diagram();
        diagram2.setId(diagram1.getId());
        assertThat(diagram1).isEqualTo(diagram2);
        diagram2.setId(2L);
        assertThat(diagram1).isNotEqualTo(diagram2);
        diagram1.setId(null);
        assertThat(diagram1).isNotEqualTo(diagram2);
    }
}
