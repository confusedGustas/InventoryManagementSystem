package org.gustas.inventory.inventorymanagementsystem.domain.auth.mapper;

import org.gustas.inventory.inventorymanagementsystem.domain.auth.dto.TokenDto;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.dto.RegisterDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.UserRole;
import org.springframework.stereotype.Component;
import java.util.Locale;

@Component
public class AuthMapper {

    public TokenDto toDto(String token) {
        return TokenDto.builder()
                .token(token)
                .build();
    }

    public User toUser(RegisterDto registerDto, String encodedPassword) {
        return User.builder()
                .firstName(registerDto.getFirstName().trim())
                .lastName(registerDto.getLastName().trim())
                .username(registerDto.getUsername().trim())
                .email(registerDto.getEmail().trim().toLowerCase(Locale.ROOT))
                .password(encodedPassword)
                .locale(Locale.ENGLISH)
                .enabled(true)
                .userRole(UserRole.COMPANY_USER)
                .build();
    }

}
