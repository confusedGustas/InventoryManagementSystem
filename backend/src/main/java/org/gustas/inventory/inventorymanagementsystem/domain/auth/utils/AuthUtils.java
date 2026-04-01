package org.gustas.inventory.inventorymanagementsystem.domain.auth.utils;

import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.UserRole;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    public boolean isPlatformAdmin(User user) {
        return user != null && user.getUserRole() == UserRole.PLATFORM_ADMIN;
    }

}
