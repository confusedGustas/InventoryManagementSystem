package org.gustas.inventory.inventorymanagementsystem.domain.auth.integrity;

import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthIntegrityTest {

    private final AuthIntegrity authIntegrity = new AuthIntegrity();

    @Test
    void shouldRejectInvalidCredentials() {
        assertThatThrownBy(() -> authIntegrity.checkCredentials(false))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    void shouldRejectDisabledUser() {
        User user = User.builder().enabled(false).build();

        assertThatThrownBy(() -> authIntegrity.checkUserEnabled(user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User account is disabled");
    }

    @Test
    void shouldAllowEnabledUser() {
        assertThatCode(() -> authIntegrity.checkUserEnabled(User.builder().enabled(true).build())).doesNotThrowAnyException();
    }
}
