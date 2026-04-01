package org.gustas.inventory.inventorymanagementsystem.domain.auth.integrity;

import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthIntegrity {

    public void checkCredentials(boolean credentialsValid) {
        if (!credentialsValid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    public void checkUserEnabled(User user) {
        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User account is disabled");
        }
    }

    public void checkUserNotNull(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

}
