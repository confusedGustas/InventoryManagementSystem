package org.gustas.inventory.inventorymanagementsystem.domain.auth.service;

import org.gustas.inventory.inventorymanagementsystem.config.jwt.JwtUtil;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.dto.TokenDto;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.integrity.AuthIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.mapper.AuthMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.integrity.UserIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.user.repository.UserRepository;
import org.gustas.inventory.inventorymanagementsystem.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthIntegrity authIntegrity;
    @Mock
    private AuthMapper authMapper;
    @Mock
    private UserIntegrity userIntegrity;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginUser() {
        User user = TestDataFactory.companyAdmin(TestDataFactory.company());
        when(userRepository.findByUsername("manager")).thenReturn(user);
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        when(jwtUtil.generateToken("manager", "COMPANY_ADMIN", "Company", "Admin")).thenReturn("token");
        when(authMapper.toDto("token")).thenReturn(TokenDto.builder().token("token").build());

        TokenDto result = authService.login(TestDataFactory.loginDto());

        assertThat(result.getToken()).isEqualTo("token");
        verify(authIntegrity).checkUserNotNull(user);
        verify(authIntegrity).checkCredentials(true);
        verify(authIntegrity).checkUserEnabled(user);
    }

    @Test
    void shouldRegisterUser() {
        User savedUser = TestDataFactory.companyUser(TestDataFactory.company());
        savedUser.setUsername("jane");
        when(passwordEncoder.encode("secret")).thenReturn("encoded");
        when(authMapper.toUser(any(), any())).thenReturn(savedUser);
        when(userRepository.save(savedUser)).thenReturn(savedUser);
        when(jwtUtil.generateToken(savedUser.getUsername(), "COMPANY_USER", savedUser.getFirstName(), savedUser.getLastName())).thenReturn("token");
        when(authMapper.toDto("token")).thenReturn(TokenDto.builder().token("token").build());

        TokenDto result = authService.register(TestDataFactory.registerDto());

        assertThat(result.getToken()).isEqualTo("token");
        verify(userIntegrity).checkUsernameAvailable(userRepository, "jane", null);
        verify(userIntegrity).checkEmailAvailable(userRepository, "jane@example.com", null);
    }
}
