package com.skillassessment.platform.dto;

import com.skillassessment.platform.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
public class AdminUpdateUserDto {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    @NotEmpty
    private Set<RoleName> roles;

    // Optional: for resetting a user's password
    @Size(min = 6, max = 40)
    private String password;
}