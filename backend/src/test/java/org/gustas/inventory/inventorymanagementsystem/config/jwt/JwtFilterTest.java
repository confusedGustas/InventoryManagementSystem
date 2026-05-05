package org.gustas.inventory.inventorymanagementsystem.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtFilterTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldPopulateSecurityContextFromBearerToken() throws ServletException, IOException {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        when(jwtUtil.extractUsername("token")).thenReturn("jane");
        when(jwtUtil.extractRole("token")).thenReturn("COMPANY_ADMIN");
        JwtFilter jwtFilter = new JwtFilter(jwtUtil);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtFilter.AUTHORIZATION_HEADER, "Bearer token");

        jwtFilter.doFilter(request, new MockHttpServletResponse(), mock(FilterChain.class));

        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("jane");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_COMPANY_ADMIN");
    }
}
