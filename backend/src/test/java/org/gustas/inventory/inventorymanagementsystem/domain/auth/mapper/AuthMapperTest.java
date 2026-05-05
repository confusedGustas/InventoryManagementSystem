package org.gustas.inventory.inventorymanagementsystem.domain.auth.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.auth.dto.TokenDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.UserRole;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class AuthMapperTest {

    private final AuthMapper authMapper = new AuthMapper();

    @Test
    void shouldMapToken() {
        TokenDto result = authMapper.toDto("jwt-token");

        assertThat(result.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void shouldNormalizeRegisterDtoToUser() {
        User result = authMapper.toUser(TestDataFactory.registerDto(), "encoded");

        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getUsername()).isEqualTo("jane");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
        assertThat(result.getPassword()).isEqualTo("encoded");
        assertThat(result.getLocale()).isEqualTo(Locale.ENGLISH);
        assertThat(result.getUserRole()).isEqualTo(UserRole.COMPANY_USER);
        assertThat(result.isEnabled()).isTrue();
    }
}
