package com.skillassessment.platform.controller;

import com.skillassessment.platform.dto.ProfileUpdateDto;
import com.skillassessment.platform.dto.QuizHistoryDto;
import com.skillassessment.platform.dto.UserDto;
import com.skillassessment.platform.entity.User;
import com.skillassessment.platform.service.QuizAttemptService;
import com.skillassessment.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling user-related operations.
 * Provides endpoints for retrieving user details and their quiz history.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final QuizAttemptService quizAttemptService;

    /**
     * GET /api/users/me : Get the details of the currently authenticated user.
     *
     * @return ResponseEntity with the UserDto of the current user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserDetails());
    }

    /**
     * GET /api/users : (Admin only) Get a list of all users in the system.
     *
     * @return ResponseEntity with a list of all UserDtos.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * GET /api/users/{id} : (Admin only) Get details of a specific user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return ResponseEntity with the UserDto of the specified user.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * GET /api/users/me/quiz-history : Get the quiz history for the currently authenticated user.
     *
     * @return ResponseEntity with a list of QuizHistoryDto objects.
     */
    @GetMapping("/me/quiz-history")
    public ResponseEntity<List<QuizHistoryDto>> getCurrentUserQuizHistory() {
        User currentUser = userService.getCurrentUserEntity();
        return ResponseEntity.ok(quizAttemptService.getQuizHistoryForUser(currentUser.getId()));
    }

    /**
     * GET /api/users/{userId}/quiz-history : Get the quiz history for a specific user.
     * Security is enforced in the service layer: a regular user can only access their own history,
     * while an admin can access any user's history.
     *
     * @param userId The ID of the user whose history is being requested.
     * @return ResponseEntity with a list of QuizHistoryDto objects.
     */
    @GetMapping("/{userId}/quiz-history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<QuizHistoryDto>> getUserQuizHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(quizAttemptService.getQuizHistoryForUser(userId));
    }

    /**
     * PUT /api/users/me : Updates the current user's profile.
     * @param updateDto The profile data to update.
     * @return The updated UserDto.
     */
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateUserProfile(@Valid @RequestBody ProfileUpdateDto updateDto) {
        return ResponseEntity.ok(userService.updateUserProfile(updateDto));
    }
}