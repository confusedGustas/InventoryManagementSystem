package org.gustas.inventory.inventorymanagementsystem.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.common.Constants;
import org.gustas.inventory.inventorymanagementsystem.common.dto.CompanyOptionDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseDto;
import org.gustas.inventory.inventorymanagementsystem.common.dto.PagedResponseWithCompaniesDto;
import org.gustas.inventory.inventorymanagementsystem.common.mapper.PagedResponseMapper;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.integrity.AuthIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.integrity.CompanyIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.company.mapper.CompanyMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.company.repository.CompanyRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.ChangeEmailDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.ChangePasswordDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.CreateUserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.UpdateUserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.UserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.UserRole;
import org.gustas.inventory.inventorymanagementsystem.domain.user.integrity.UserIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.user.mapper.UserMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserIntegrity userIntegrity;
    private final CompanyIntegrity companyIntegrity;
    private final UserMapper userMapper;
    private final PagedResponseMapper pagedResponseMapper;
    private final CurrentUserContext currentUserContext;
    private final AuthUtils authUtils;
    private final AuthIntegrity authIntegrity;
    private final CompanyMapper companyMapper;

    @Transactional(readOnly = true)
    public PagedResponseWithCompaniesDto<UserDto, CompanyOptionDto> getUsers(Integer page, String username) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Constants.DEFAULT_PAGE_SIZE);
        User currentUser = currentUserContext.getCurrentUser(username);
        Page<UserDto> userPage = authUtils.isPlatformAdmin(currentUser)
                ? userRepository.findAll(pageable).map(userMapper::toDto)
                : userRepository.findAllByCompanyId(currentUserContext.getManagedCompanyId(currentUser), pageable).map(userMapper::toDto);
        PagedResponseDto<UserDto> pagedUsers = pagedResponseMapper.toDto(userPage);
        List<CompanyOptionDto> companies = currentUserContext.resolveManageableCompanies(currentUser, companyMapper::toCompanyOptionDto);

        return pagedResponseMapper.toPagedWithCompanies(pagedUsers, companies);
    }

    @Transactional(readOnly = true)
    public UserDto getProfile(String username) {
        User user = userRepository.findByUsername(username);

        userIntegrity.checkUserNotNull(user);

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto changePassword(String username, ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByUsername(username);

        userIntegrity.checkUserNotNull(user);

        boolean passwordMatches = passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword());
        authIntegrity.checkCredentials(passwordMatches);

        String encodedPassword = passwordEncoder.encode(changePasswordDto.getNewPassword());
        user.setPassword(encodedPassword);

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Transactional
    public UserDto changeEmail(String username, ChangeEmailDto changeEmailDto) {
        User user = userRepository.findByUsername(username);

        userIntegrity.checkUserNotNull(user);

        String email = changeEmailDto.getEmail().trim().toLowerCase(Locale.ROOT);
        userIntegrity.checkEmailAvailable(userRepository, email, user.getId());

        user.setEmail(email);

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Transactional
    public UserDto createUser(CreateUserDto createUserDto, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        Company company = resolveCompany(createUserDto.getCompanyId(), currentUser, createUserDto.getUserRole());
        String normalizedUsername = createUserDto.getUsername().trim();
        String email = createUserDto.getEmail().trim().toLowerCase(Locale.ROOT);

        userIntegrity.checkUsernameAvailable(userRepository, normalizedUsername, null);
        userIntegrity.checkEmailAvailable(userRepository, email, null);

        User user = userMapper.toUser(createUserDto, passwordEncoder.encode(createUserDto.getPassword()), company);
        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Transactional
    public UserDto updateUser(UUID userId, UpdateUserDto updateUserDto, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        User user = currentUserContext.findManagedEntity(
                currentUser,
                userId,
                userRepository::findUserById,
                userRepository::findUserByIdAndCompanyId
        );
        Company company = resolveCompany(updateUserDto.getCompanyId(), currentUser, updateUserDto.getUserRole());

        userIntegrity.checkUserNotNull(user);
        userIntegrity.checkManagedUserRole(user);
        userIntegrity.checkUsernameAvailable(userRepository, updateUserDto.getUsername().trim(), user.getId());
        userIntegrity.checkEmailAvailable(userRepository, updateUserDto.getEmail().trim().toLowerCase(Locale.ROOT), user.getId());

        userMapper.updateUser(user, updateUserDto, company);

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto disableUser(UUID userId, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        User user = currentUserContext.findManagedEntity(
                currentUser,
                userId,
                userRepository::findUserById,
                userRepository::findUserByIdAndCompanyId
        );

        userIntegrity.checkUserNotNull(user);
        userIntegrity.checkManagedUserRole(user);
        userIntegrity.checkUserCanDisable(user);
        user.setEnabled(false);

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto enableUser(UUID userId, String username) {
        User currentUser = currentUserContext.getCurrentUser(username);
        User user = currentUserContext.findManagedEntity(
                currentUser,
                userId,
                userRepository::findUserById,
                userRepository::findUserByIdAndCompanyId
        );

        userIntegrity.checkUserNotNull(user);
        userIntegrity.checkManagedUserRole(user);
        userIntegrity.checkUserCanEnable(user);
        user.setEnabled(true);

        return userMapper.toDto(userRepository.save(user));
    }

    private Company resolveCompany(UUID companyId, User currentUser, UserRole targetRole) {
        userIntegrity.checkPlatformAdminAssignmentAllowed(currentUser, targetRole);

        if (targetRole == UserRole.PLATFORM_ADMIN) {
            return null;
        }

        return currentUserContext.resolveCompany(companyId, currentUser);
    }

}
