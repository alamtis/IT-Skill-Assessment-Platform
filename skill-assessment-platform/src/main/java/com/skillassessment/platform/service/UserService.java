package com.skillassessment.platform.service;

import com.skillassessment.platform.dto.*;
import com.skillassessment.platform.entity.QuizAttempt;
import com.skillassessment.platform.entity.Role;
import com.skillassessment.platform.entity.User;
import com.skillassessment.platform.enums.RoleName;
import com.skillassessment.platform.exception.ApiException;
import com.skillassessment.platform.exception.ResourceNotFoundException;
import com.skillassessment.platform.repository.RoleRepository;
import com.skillassessment.platform.repository.UserRepository;
import com.skillassessment.platform.repository.QuizAttemptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service layer for handling user-related business logic,
 * such as retrieving user data and managing roles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final QuizAttemptRepository quizAttemptRepository;

    private final PasswordEncoder passwordEncoder;


    // --- Public Methods ---

    /**
     * Retrieves the details of the currently authenticated user.
     * @return A DTO containing the current user's information.
     */
    @Transactional(readOnly = true)
    public UserDto getCurrentUserDetails() {
        User currentUser = getCurrentUserEntity();
        return mapToUserDto(currentUser);
    }

    /**
     * Retrieves a list of all users in the system. (Admin only)
     * @return A list of all user DTOs.
     */
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific user by their ID. (Admin only)
     * @param id The ID of the user to find.
     * @return A DTO of the specified user.
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToUserDto(user);
    }

    /**
     * Assigns a set of roles to a user. (Admin only)
     * Replaces any existing roles with the new set.
     * @param roleAssignDto DTO containing the user ID and the roles to assign.
     * @return A DTO of the updated user.
     */
    @Transactional
    public UserDto assignRoles(RoleAssignDto roleAssignDto) {
        User user = userRepository.findById(roleAssignDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", roleAssignDto.getUserId()));

        Set<Role> assignedRoles = roleAssignDto.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName.name())))
                .collect(Collectors.toSet());

        user.setRoles(assignedRoles);
        User updatedUser = userRepository.save(user);

        log.info("Updated roles for user '{}' to: {}", user.getUsername(), assignedRoles.stream().map(Role::getName).collect(Collectors.toSet()));
        return mapToUserDto(updatedUser);
    }

    /**
     * Retrieves analytics data for all users, including their quiz attempt history.
     * @return A list of UserAnalyticsDto objects.
     */
    @Transactional(readOnly = true)
    public List<UserAnalyticsDto> getAllUserAnalytics() {
        List<User> users = userRepository.findAllWithRoles();

        return users.stream().map(user -> {
            // For each user, fetch their quiz attempts
            List<QuizAttempt> attempts = quizAttemptRepository.findByUserIdOrderByCompletedAtDesc(user.getId());

            // Map the attempts to the summary DTO
            List<UserAnalyticsDto.QuizAttemptSummaryDto> attemptSummaries = attempts.stream()
                    .map(attempt -> UserAnalyticsDto.QuizAttemptSummaryDto.builder()
                            .attemptId(attempt.getId())
                            .quizTitle(attempt.getQuiz().getTitle())
                            .percentage(attempt.getPercentage())
                            .completedAt(attempt.getCompletedAt())
                            .build())
                    .collect(Collectors.toList());

            // Build the final DTO for the user
            return UserAnalyticsDto.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .attempts(attemptSummaries)
                    .build();
        }).collect(Collectors.toList());
    }
    // --- Helper Methods ---

    /**
     * Retrieves the full User entity object for the currently authenticated user.
     * This is an internal helper method to be used by other services or controllers.
     * @return The User entity from the database.
     */
    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "User is not authenticated. Please log in.");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    /**
     * Maps a User entity to a UserDto.
     * @param user The User entity to map.
     * @return A corresponding UserDto.
     */
    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        userDto.setCreatedAt(user.getCreatedAt());
        return userDto;
    }

    /**
     * Updates the profile of the currently authenticated user.
     * @param updateDto DTO containing the fields to update (email, password).
     * @return The updated UserDto.
     */
    @Transactional
    public UserDto updateUserProfile(ProfileUpdateDto updateDto) {
        User currentUser = getCurrentUserEntity();

        if (updateDto.getUsername() != null && !updateDto.getUsername().isBlank()) {
            // Check if the new username is already taken by ANOTHER user
            userRepository.findByUsername(updateDto.getUsername()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(currentUser.getId())) {
                    throw new ApiException(HttpStatus.CONFLICT, "Username is already taken by another account.");
                }
            });
            currentUser.setUsername(updateDto.getUsername());
        }

        // Update email if provided and different
        if (updateDto.getEmail() != null && !updateDto.getEmail().isBlank()) {
            // Check if the new email is already taken by another user
            userRepository.findByEmail(updateDto.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(currentUser.getId())) {
                    throw new ApiException(HttpStatus.CONFLICT, "Email is already in use by another account.");
                }
            });
            currentUser.setEmail(updateDto.getEmail());
        }

        // Update password if provided
        if (updateDto.getPassword() != null && !updateDto.getPassword().isBlank()) {
            currentUser.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        User updatedUser = userRepository.save(currentUser);
        log.info("User '{}' updated their profile.", currentUser.getUsername());
        return mapToUserDto(updatedUser);
    }

    /**
     * (Admin only) Creates a new user with specified roles.
     * @param createUserDto DTO containing the new user's details and roles.
     * @return The created UserDto.
     */
    @Transactional
    public UserDto adminCreateUser(AdminCreateUserDto createUserDto) {
        // Step 1: Validate for uniqueness in a single, clean block.
        validateUserUniqueness(createUserDto.getUsername(), createUserDto.getEmail());

        // Step 2: Delegate the creation and mapping to a helper method.
        User newUser = createNewUserFromDto(createUserDto);

        // Step 3: Save the new user.
        User savedUser = userRepository.save(newUser);

        log.info("Admin created new user '{}' with roles: {}", savedUser.getUsername(),
                savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        return mapToUserDto(savedUser);
    }

    /**
     * Checks if a given username or email already exists in the database.
     * Throws an ApiException if a conflict is found.
     * @param username The username to check.
     * @param email The email to check.
     */
    private void validateUserUniqueness(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new ApiException(HttpStatus.CONFLICT, "Username is already taken!");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already in use!");
        }
    }

    /**
     * Creates and populates a new User entity from an AdminCreateUserDto.
     * This method encapsulates the mapping logic.
     * @param dto The DTO containing the user data.
     * @return A new, populated User entity (not yet persisted).
     */
    private User createNewUserFromDto(AdminCreateUserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        Set<Role> roles = dto.getRoles().stream()
                .map(this::findRoleByName)
                .collect(Collectors.toSet());
        user.setRoles(roles);

        return user;
    }

    /**
     * (Admin only) Updates a user's details and roles.
     * @param userId The ID of the user to update.
     * @param updateUserDto The DTO with the updated data.
     * @return The updated UserDto.
     */
    @Transactional
    public UserDto adminUpdateUser(Long userId, AdminUpdateUserDto updateUserDto) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check for username conflict
        userRepository.findByUsername(updateUserDto.getUsername()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(userId)) {
                throw new ApiException(HttpStatus.CONFLICT, "Username is already taken by another user.");
            }
        });
        userToUpdate.setUsername(updateUserDto.getUsername());

        // Check for email conflict
        userRepository.findByEmail(updateUserDto.getEmail()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(userId)) {
                throw new ApiException(HttpStatus.CONFLICT, "Email is already in use by another user.");
            }
        });
        userToUpdate.setEmail(updateUserDto.getEmail());

        // Update roles
        Set<Role> roles = updateUserDto.getRoles().stream()
                .map(this::findRoleByName)
                .collect(Collectors.toSet());
        userToUpdate.setRoles(roles);

        // Optionally update password if a new one is provided
        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().isBlank()) {
            userToUpdate.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
        }

        User savedUser = userRepository.save(userToUpdate);
        log.info("Admin updated profile for user '{}'", savedUser.getUsername());
        return mapToUserDto(savedUser);
    }
    /**
     * Finds a Role by its RoleName or throws a ResourceNotFoundException.
     * @param roleName The name of the role to find.
     * @return The found Role entity.
     */
    private Role findRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName.name()));
    }
}