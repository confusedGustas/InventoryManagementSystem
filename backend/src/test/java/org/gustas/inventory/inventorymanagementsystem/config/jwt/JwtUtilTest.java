package org.gustas.inventory.inventorymanagementsystem.config.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String SECRET = "01234567890123456789012345678901";

    @Test
    void shouldGenerateAndParseToken() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "SECRET", SECRET);

        String token = jwtUtil.generateToken("jane", "COMPANY_ADMIN", "Jane", "Doe");

        assertThat(jwtUtil.extractUsername(token)).isEqualTo("jane");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("COMPANY_ADMIN");
        assertThat(jwtUtil.extractClaims(token).get(JwtUtil.FIRST_NAME_CLAIM, String.class)).isEqualTo("Jane");
    }
}
