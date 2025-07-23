package com.skillassessment.platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skillassessment.platform.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * A Data Transfer Object (DTO) for representing a User in API responses.
 * This DTO is designed to be a safe representation of a User entity,
 * deliberately excluding sensitive information such as the password.
 * It transforms the set of Role entities into a more client-friendly
 * set of role names (enums).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Omit null fields (like updatedAt) from JSON output
public class UserDto {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The user's username.
     */
    private String username;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * A set of roles assigned to the user (e.g., "ROLE_USER", "ROLE_ADMIN").
     */
    private Set<RoleName> roles;

    /**
     * The timestamp when the user account was created.
     */
    private LocalDateTime createdAt;

    /**
     * The timestamp of the last update to the user's account.
     * This field will be omitted from the JSON if it is null.
     */
    private LocalDateTime updatedAt;
}