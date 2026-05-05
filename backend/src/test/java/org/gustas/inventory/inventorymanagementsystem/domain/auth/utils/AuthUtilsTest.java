package org.gustas.inventory.inventorymanagementsystem.domain.auth.utils;

import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.UserRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthUtilsTest {

    private final AuthUtils authUtils = new AuthUtils();

    @Test
    void shouldDetectPlatformAdmin() {
        User user = User.builder().userRole(UserRole.PLATFORM_ADMIN).build();

        assertThat(authUtils.isPlatformAdmin(user)).isTrue();
        assertThat(authUtils.isCompanyAdmin(user)).isFalse();
    }

    @Test
    void shouldDetectCompanyAdmin() {
        User user = User.builder().userRole(UserRole.COMPANY_ADMIN).build();

        assertThat(authUtils.isCompanyAdmin(user)).isTrue();
        assertThat(authUtils.isPlatformAdmin(user)).isFalse();
    }
}
