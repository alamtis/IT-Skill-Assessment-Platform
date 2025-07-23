package com.skillassessment.platform.controller;

import com.skillassessment.platform.dto.JwtAuthResponseDto;
import com.skillassessment.platform.dto.LoginDto;
import com.skillassessment.platform.dto.RegisterDto;
import com.skillassessment.platform.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user authentication endpoints like login and registration.
 * These endpoints are publicly accessible.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/login : Authenticates a user and returns a JWT token.
     *
     * @param loginDto DTO containing the user's username and password.
     * @return ResponseEntity with a JwtAuthResponseDto containing the access token.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);

        JwtAuthResponseDto jwtAuthResponse = new JwtAuthResponseDto();
        jwtAuthResponse.setAccessToken(token);
        // The token type is a standard part of the Bearer token scheme.
        jwtAuthResponse.setTokenType("Bearer");

        return ResponseEntity.ok(jwtAuthResponse);
    }

    /**
     * POST /api/auth/register : Registers a new user in the system.
     *
     * @param registerDto DTO containing the new user's username, email, and password.
     * @return ResponseEntity with a success message and HTTP status 201 (Created).
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto registerDto) {
        String responseMessage = authService.register(registerDto);
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }
}