package org.gustas.inventory.inventorymanagementsystem.domain.user.service;

import org.gustas.inventory.inventorymanagementsystem.common.mapper.PagedResponseMapper;
import org.gustas.inventory.inventorymanagementsystem.common.service.CurrentUserContext;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.integrity.AuthIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.utils.AuthUtils;
import org.gustas.inventory.inventorymanagementsystem.domain.company.entity.Company;
import org.gustas.inventory.inventorymanagementsystem.domain.company.integrity.CompanyIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.company.mapper.CompanyMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.company.repository.CompanyRepository;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.ChangePasswordDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.CreateUserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.UpdateUserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.dto.UserDto;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.integrity.UserIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.user.mapper.UserMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.user.repository.UserRepository;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserIntegrity userIntegrity;
    @Mock
    private CompanyIntegrity companyIntegrity;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PagedResponseMapper pagedResponseMapper;
    @Mock
    private CurrentUserContext currentUserContext;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private AuthIntegrity authIntegrity;
    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldChangeEmailWithNormalization() {
        User user = TestDataFactory.companyUser(TestDataFactory.company());
        when(userRepository.findByUsername("employee")).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(UserDto.builder().email("new@example.com").build());

        UserDto result = userService.changeEmail("employee", TestDataFactory.changeEmailDto());

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(userIntegrity).checkEmailAvailable(userRepository, "new@example.com", user.getId());
    }

    @Test
    void shouldReturnCompanyScopedUsers() {
        Company company = TestDataFactory.company();
        User currentUser = TestDataFactory.companyAdmin(company);
        User managedUser = TestDataFactory.companyUser(company);
        UserDto dto = UserDto.builder().username("employee").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(currentUser);
        when(authUtils.isPlatformAdmin(currentUser)).thenReturn(false);
        when(currentUserContext.getManagedCompanyId(currentUser)).thenReturn(company.getId());
        when(userRepository.findAllByCompanyId(eq(company.getId()), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(managedUser)));
        when(userMapper.toDto(managedUser)).thenReturn(dto);
        when(pagedResponseMapper.toDto(any())).thenCallRealMethod();
        when(currentUserContext.resolveManageableCompanies(eq(currentUser), org.mockito.ArgumentMatchers.<Function<Company, ?>>any())).thenReturn(List.of());
        when(pagedResponseMapper.toPagedWithCompanies(any(), any())).thenCallRealMethod();

        var result = userService.getUsers(1, "manager");

        assertThat(result.getContent()).containsExactly(dto);
    }

    @Test
    void shouldGetProfile() {
        User user = TestDataFactory.companyUser(TestDataFactory.company());
        UserDto dto = UserDto.builder().username("employee").build();
        when(userRepository.findByUsername("employee")).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.getProfile("employee");

        assertThat(result).isSameAs(dto);
        verify(userIntegrity).checkUserNotNull(user);
    }

    @Test
    void shouldChangePassword() {
        User user = TestDataFactory.companyUser(TestDataFactory.company());
        ChangePasswordDto changePasswordDto = TestDataFactory.changePasswordDto();
        when(userRepository.findByUsername("employee")).thenReturn(user);
        when(passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(changePasswordDto.getNewPassword())).thenReturn("new-encoded");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(UserDto.builder().username("employee").build());

        UserDto result = userService.changePassword("employee", changePasswordDto);

        assertThat(result.getUsername()).isEqualTo("employee");
        assertThat(user.getPassword()).isEqualTo("new-encoded");
        verify(authIntegrity).checkCredentials(true);
    }

    @Test
    void shouldCreateUser() {
        Company company = TestDataFactory.company();
        User currentUser = TestDataFactory.companyAdmin(company);
        CreateUserDto createUserDto = TestDataFactory.createUserDto(company.getId());
        User createdUser = TestDataFactory.companyUser(company);
        UserDto dto = UserDto.builder().username("employee").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(currentUser);
        when(currentUserContext.resolveCompany(company.getId(), currentUser)).thenReturn(company);
        when(passwordEncoder.encode(createUserDto.getPassword())).thenReturn("encoded");
        when(userMapper.toUser(createUserDto, "encoded", company)).thenReturn(createdUser);
        when(userRepository.save(createdUser)).thenReturn(createdUser);
        when(userMapper.toDto(createdUser)).thenReturn(dto);

        UserDto result = userService.createUser(createUserDto, "manager");

        assertThat(result).isSameAs(dto);
        verify(userIntegrity).checkPlatformAdminAssignmentAllowed(currentUser, createUserDto.getUserRole());
        verify(userIntegrity).checkUsernameAvailable(userRepository, "jane", null);
        verify(userIntegrity).checkEmailAvailable(userRepository, "jane@example.com", null);
    }

    @Test
    void shouldUpdateUser() {
        Company company = TestDataFactory.company();
        User currentUser = TestDataFactory.companyAdmin(company);
        User managedUser = TestDataFactory.companyUser(company);
        UpdateUserDto updateUserDto = TestDataFactory.updateUserDto(company.getId());
        UserDto dto = UserDto.builder().username("janet").build();
        when(currentUserContext.getCurrentUser("manager")).thenReturn(currentUser);
        when(currentUserContext.findManagedEntity(any(), eq(managedUser.getId()), any(), any())).thenReturn(managedUser);
        when(currentUserContext.resolveCompany(company.getId(), currentUser)).thenReturn(company);
        when(userRepository.save(managedUser)).thenReturn(managedUser);
        when(userMapper.toDto(managedUser)).thenReturn(dto);

        UserDto result = userService.updateUser(managedUser.getId(), updateUserDto, "manager");

        assertThat(result).isSameAs(dto);
        verify(userIntegrity).checkManagedUserRole(managedUser);
        verify(userMapper).updateUser(managedUser, updateUserDto, company);
    }

    @Test
    void shouldDisableUser() {
        Company company = TestDataFactory.company();
        User currentUser = TestDataFactory.companyAdmin(company);
        User managedUser = TestDataFactory.companyUser(company);
        when(currentUserContext.getCurrentUser("manager")).thenReturn(currentUser);
        when(currentUserContext.findManagedEntity(any(), eq(managedUser.getId()), any(), any())).thenReturn(managedUser);
        when(userRepository.save(managedUser)).thenReturn(managedUser);
        when(userMapper.toDto(managedUser)).thenReturn(UserDto.builder().enabled(false).build());

        UserDto result = userService.disableUser(managedUser.getId(), "manager");

        assertThat(managedUser.isEnabled()).isFalse();
        assertThat(result.isEnabled()).isFalse();
        verify(userIntegrity).checkUserCanDisable(managedUser);
    }

    @Test
    void shouldEnableUser() {
        Company company = TestDataFactory.company();
        User currentUser = TestDataFactory.companyAdmin(company);
        User managedUser = TestDataFactory.companyUser(company);
        managedUser.setEnabled(false);
        when(currentUserContext.getCurrentUser("manager")).thenReturn(currentUser);
        when(currentUserContext.findManagedEntity(any(), eq(managedUser.getId()), any(), any())).thenReturn(managedUser);
        when(userRepository.save(managedUser)).thenReturn(managedUser);
        when(userMapper.toDto(managedUser)).thenReturn(UserDto.builder().enabled(true).build());

        UserDto result = userService.enableUser(managedUser.getId(), "manager");

        assertThat(managedUser.isEnabled()).isTrue();
        assertThat(result.isEnabled()).isTrue();
        verify(userIntegrity).checkUserCanEnable(managedUser);
    }
}
