package com.skillassessment.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateDto {

    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    private String username;

    @Email(message = "Email should be valid.")
    @Size(max = 50, message = "Email cannot exceed 50 characters.")
    private String email;

    // Optional: Allow password change
    @Size(min = 6, max = 40, message = "Password must be between 6 and 40 characters.")
    private String password;

}