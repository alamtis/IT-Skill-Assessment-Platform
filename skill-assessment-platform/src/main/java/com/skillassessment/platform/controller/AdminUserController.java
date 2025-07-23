package com.skillassessment.platform.controller;

import com.skillassessment.platform.dto.*;
import com.skillassessment.platform.service.QuizAttemptService;
import com.skillassessment.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for administrative actions related to user management.
 * All endpoints in this controller require ADMIN role.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final QuizAttemptService quizAttemptService;

    /**
     * POST /api/admin/roles/assign : Assign or update roles for a specific user.
     * This action is restricted to administrators.
     *
     * @param roleAssignDto DTO containing the userId and the list of roles to assign.
     * @return ResponseEntity with the updated UserDto.
     */
    @PostMapping("/roles/assign")
    public ResponseEntity<UserDto> assignRolesToUser(@Valid @RequestBody RoleAssignDto roleAssignDto) {
        UserDto updatedUser = userService.assignRoles(roleAssignDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * GET /api/admin/analytics/users : Get analytics for all users.
     * @return A list of users with their quiz attempt histories.
     */
    @GetMapping("/analytics/users")
    public ResponseEntity<List<UserAnalyticsDto>> getUserAnalytics() {
        return ResponseEntity.ok(userService.getAllUserAnalytics());
    }

    /**
     * POST /api/admin/users : (Admin only) Creates a new user.
     * @param createUserDto The new user's details.
     * @return The created UserDto.
     */
    @PostMapping("/users")
    public ResponseEntity<UserDto> adminCreateUser(@Valid @RequestBody AdminCreateUserDto createUserDto) {
        return new ResponseEntity<>(userService.adminCreateUser(createUserDto), HttpStatus.CREATED);
    }

    /**
     * PUT /api/admin/users/{userId} : (Admin only) Updates a user.
     * @param userId The ID of the user to update.
     * @param updateUserDto The user's updated details.
     * @return The updated UserDto.
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDto> adminUpdateUser(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUpdateUserDto updateUserDto) {
        return ResponseEntity.ok(userService.adminUpdateUser(userId, updateUserDto));
    }

    /**
     * DELETE /api/admin/attempts/{attemptId} : (Admin only) Deletes a quiz attempt.
     * @param attemptId The ID of the quiz attempt to delete.
     * @return ResponseEntity with no content.
     */
    @DeleteMapping("/attempts/{attemptId}")
    public ResponseEntity<Void> deleteQuizAttempt(@PathVariable Long attemptId) {
        quizAttemptService.deleteQuizAttempt(attemptId);
        return ResponseEntity.noContent().build();
    }
}