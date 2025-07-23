package com.skillassessment.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for handling user login requests.
 * This class is used as the request body for the /api/auth/login endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    /**
     * The username of the user attempting to log in.
     * This field is mandatory and cannot be empty or just whitespace.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * The password of the user attempting to log in.
     * This field is mandatory and cannot be empty or just whitespace.
     */
    @NotBlank(message = "Password is required")
    private String password;
}