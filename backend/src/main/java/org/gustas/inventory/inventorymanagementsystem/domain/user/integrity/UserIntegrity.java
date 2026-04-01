package org.gustas.inventory.inventorymanagementsystem.domain.user.integrity;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.UserRole;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserIntegrity {

    private final AuthUtils authUtils;

    public void checkUserNotNull(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    public void checkEmailAvailable(UserRepository userRepository, String email, UUID excludedUserId) {
        boolean emailTaken = excludedUserId == null
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndIdNot(email, excludedUserId);

        if (emailTaken) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already taken");
        }
    }

    public void checkUsernameAvailable(UserRepository userRepository, String username, UUID excludedUserId) {
        boolean usernameTaken = excludedUserId == null
                ? userRepository.existsByUsername(username)
                : userRepository.existsByUsernameAndIdNot(username, excludedUserId);

        if (usernameTaken) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }
    }

    public void checkUserCanDisable(User user) {
        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already disabled");
        }
    }

    public void checkUserCanEnable(User user) {
        if (user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already enabled");
        }
    }

    public void checkManagedUserRole(User user) {
        if (user.getUserRole() == UserRole.PLATFORM_ADMIN && "admin".equalsIgnoreCase(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Default admin user cannot be modified here");
        }
    }

    public void checkPlatformAdminAssignmentAllowed(User currentUser, UserRole targetRole) {
        if (targetRole == UserRole.PLATFORM_ADMIN && !authUtils.isPlatformAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only platform admins can manage platform admins");
        }
    }

}
