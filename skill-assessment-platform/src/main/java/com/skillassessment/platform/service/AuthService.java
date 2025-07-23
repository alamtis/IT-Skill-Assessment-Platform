package com.skillassessment.platform.service;

import com.skillassessment.platform.dto.LoginDto;
import com.skillassessment.platform.dto.RegisterDto;
import com.skillassessment.platform.entity.Role;
import com.skillassessment.platform.entity.User;
import com.skillassessment.platform.enums.RoleName;
import com.skillassessment.platform.exception.ApiException;
import com.skillassessment.platform.repository.RoleRepository;
import com.skillassessment.platform.repository.UserRepository;
import com.skillassessment.platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Service layer for handling user authentication and registration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Authenticates a user and generates a JWT upon successful authentication.
     *
     * @param loginDto DTO containing login credentials.
     * @return A signed JWT string.
     */
    public String login(LoginDto loginDto) {
        // The AuthenticationManager will use the UserDetailsService and PasswordEncoder
        // to validate the credentials.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        // If authentication is successful, set the authentication object in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("User '{}' logged in successfully.", loginDto.getUsername());

        // Generate and return the JWT
        return jwtTokenProvider.generateToken(authentication);
    }

    /**
     * Registers a new user, hashes their password, and assigns the default USER role.
     * This operation is transactional.
     *
     * @param registerDto DTO containing new user information.
     * @return A success message.
     */
    @Transactional
    public String register(RegisterDto registerDto) {
        // Check if username already exists in the database
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username is already taken!");
        }

        // Check if email already exists in the database
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already in use!");
        }

        // Create a new User entity
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        // Assign the default role (ROLE_USER)
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ApiException("Default user role not found in database. Please run initializers."));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        // Save the new user to the database
        userRepository.save(user);
        log.info("New user registered: {}", user.getUsername());

        return "User registered successfully.";
    }
}