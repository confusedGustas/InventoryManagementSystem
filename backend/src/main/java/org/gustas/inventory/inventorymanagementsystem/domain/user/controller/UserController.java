package org.gustas.inventory.inventorymanagementsystem.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseWithCompaniesDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.ChangeEmailDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.ChangePasswordDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.CreateUserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.UpdateUserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.UserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
    public PagedResponseWithCompaniesDto<UserDto, CompanyOptionDto> getUsers(
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "1") Integer page
    ) {
        return userService.getUsers(page, authentication.getName());
    }

    @GetMapping("/profile")
    public UserDto getProfile(Authentication authentication) {
        return userService.getProfile(authentication.getName());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
    public UserDto createUser(Authentication authentication, @Valid @RequestBody CreateUserDto createUserDto) {
        return userService.createUser(createUserDto, authentication.getName());
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
    public UserDto updateUser(Authentication authentication, @PathVariable UUID userId, @Valid @RequestBody UpdateUserDto updateUserDto) {
        return userService.updateUser(userId, updateUserDto, authentication.getName());
    }

    @PatchMapping("/change-password")
    public UserDto changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        return userService.changePassword(authentication.getName(), changePasswordDto);
    }

    @PatchMapping("/email")
    public UserDto changeEmail(Authentication authentication, @Valid @RequestBody ChangeEmailDto changeEmailDto) {
        return userService.changeEmail(authentication.getName(), changeEmailDto);
    }

    @PatchMapping("/{userId}/disable")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
    public UserDto disableUser(Authentication authentication, @PathVariable UUID userId) {
        return userService.disableUser(userId, authentication.getName());
    }

    @PatchMapping("/{userId}/enable")
    @PreAuthorize("hasAnyRole('PLATFORM_ADMIN', 'COMPANY_ADMIN')")
    public UserDto enableUser(Authentication authentication, @PathVariable UUID userId) {
        return userService.enableUser(userId, authentication.getName());
    }

}
