package com.skillassessment.platform.dto;

import com.skillassessment.platform.enums.RoleName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Data Transfer Object (DTO) for assigning one or more roles to a user.
 * This is an administrator-only action.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignDto {

    /**
     * The unique identifier of the user to whom the roles will be assigned.
     * This field is mandatory.
     */
    @NotNull(message = "User ID cannot be null.")
    private Long userId;

    /**
     * A set of roles to be assigned to the user.
     * This will replace the user's existing roles.
     * The set must contain at least one role.
     */
    @NotEmpty(message = "Roles set cannot be empty.")
    private Set<RoleName> roles;
}