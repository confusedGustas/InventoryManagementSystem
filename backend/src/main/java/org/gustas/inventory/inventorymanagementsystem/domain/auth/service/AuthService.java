package org.gustas.inventory.inventorymanagementsystem.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.gustas.inventory.inventorymanagementsystem.config.jwt.JwtUtil;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.dto.TokenDto;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.dto.LoginDto;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.dto.RegisterDto;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.integrity.AuthIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.auth.mapper.AuthMapper;
import org.gustas.inventory.inventorymanagementsystem.domain.user.entity.User;
import org.gustas.inventory.inventorymanagementsystem.domain.user.integrity.UserIntegrity;
import org.gustas.inventory.inventorymanagementsystem.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthIntegrity authIntegrity;
    private final AuthMapper authMapper;
    private final UserIntegrity userIntegrity;

    public TokenDto login(LoginDto loginDto) {
        String username = loginDto.getUsername();
        String rawPassword = loginDto.getPassword();

        User user = userRepository.findByUsername(username);

        authIntegrity.checkUserNotNull(user);

        boolean passwordMatches = passwordEncoder.matches(rawPassword, user.getPassword());
        authIntegrity.checkCredentials(passwordMatches);

        authIntegrity.checkUserEnabled(user);

        String role = user.getUserRole().name();
        String token = jwtUtil.generateToken(username, role, user.getFirstName(), user.getLastName());

        return authMapper.toDto(token);
    }

    public TokenDto register(RegisterDto registerDto) {
        String username = registerDto.getUsername().trim();
        String email = registerDto.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        String encodedPassword = passwordEncoder.encode(registerDto.getPassword());

        userIntegrity.checkUsernameAvailable(userRepository, username, null);
        userIntegrity.checkEmailAvailable(userRepository, email, null);

        User user = authMapper.toUser(registerDto, encodedPassword);
        User savedUser = userRepository.save(user);

        String role = savedUser.getUserRole().name();
        String token = jwtUtil.generateToken(savedUser.getUsername(), role, savedUser.getFirstName(), savedUser.getLastName());

        return authMapper.toDto(token);
    }

}
