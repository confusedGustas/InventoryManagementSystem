package org.gustas.inventory.inventorymanagementsystem.domain.user.integrity;

import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.UserRole;
import org.gustas.inventory.inventorymanagementsystem.domain.user.repository.UserRepository;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class UserIntegrityTest {

    private final AuthUtils authUtils = Mockito.mock(AuthUtils.class);
    private final UserIntegrity userIntegrity = new UserIntegrity(authUtils);

    @Test
    void shouldRejectDuplicateUsername() {
        UserRepository repository = Mockito.mock(UserRepository.class);
        when(repository.existsByUsername("jane")).thenReturn(true);

        assertThatThrownBy(() -> userIntegrity.checkUsernameAvailable(repository, "jane", null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Username is already taken");
    }

    @Test
    void shouldRejectPlatformAdminAssignmentForCompanyAdmin() {
        when(authUtils.isPlatformAdmin(TestDataFactory.companyAdmin(TestDataFactory.company()))).thenReturn(false);

        assertThatThrownBy(() -> userIntegrity.checkPlatformAdminAssignmentAllowed(TestDataFactory.companyAdmin(TestDataFactory.company()), UserRole.PLATFORM_ADMIN))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Only platform admins can manage platform admins");
    }

    @Test
    void shouldRejectDefaultAdminModification() {
        User admin = TestDataFactory.platformAdmin();

        assertThatThrownBy(() -> userIntegrity.checkManagedUserRole(admin))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Default admin user cannot be modified");
    }
}
