package com.skillassessment.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for handling new user registration requests.
 * This class is used as the request body for the /api/auth/register endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    /**
     * The desired username for the new user.
     * It must be between 3 and 20 characters long.
     */
    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    private String username;

    /**
     * The email address for the new user.
     * It must be a well-formed email address.
     */
    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be valid.")
    @Size(max = 50, message = "Email cannot exceed 50 characters.")
    private String email;

    /**
     * The password for the new user.
     * It must be between 6 and 40 characters long for basic security.
     */
    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters.")
    private String password;
}