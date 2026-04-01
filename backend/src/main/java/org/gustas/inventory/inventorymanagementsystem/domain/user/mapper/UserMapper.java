package org.gustas.inventory.inventorymanagementsystem.domain.user.mapper;

import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseDto;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.CreateUserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.UpdateUserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.UserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.springframework.stereotype.Component;
import java.util.Locale;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .userRole(user.getUserRole().name())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .locale(user.getLocale())
                .enabled(user.isEnabled())
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .companyName(user.getCompany() != null ? user.getCompany().getName() : null)
                .createdOn(user.getCreatedOn())
                .build();
    }

    public User toUser(CreateUserDto createUserDto, String encodedPassword, Company company) {
        return User.builder()
                .firstName(createUserDto.getFirstName().trim())
                .lastName(createUserDto.getLastName().trim())
                .username(createUserDto.getUsername().trim())
                .email(createUserDto.getEmail().trim().toLowerCase(Locale.ROOT))
                .password(encodedPassword)
                .locale(Locale.ENGLISH)
                .enabled(true)
                .userRole(createUserDto.getUserRole())
                .company(company)
                .build();
    }

    public void updateUser(User user, UpdateUserDto updateUserDto, Company company) {
        user.setFirstName(updateUserDto.getFirstName().trim());
        user.setLastName(updateUserDto.getLastName().trim());
        user.setUsername(updateUserDto.getUsername().trim());
        user.setEmail(updateUserDto.getEmail().trim().toLowerCase(Locale.ROOT));
        user.setUserRole(updateUserDto.getUserRole());
        user.setCompany(company);
    }

}
