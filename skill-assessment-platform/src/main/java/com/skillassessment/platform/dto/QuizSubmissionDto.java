package com.skillassessment.platform.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for submitting a completed quiz attempt.
 * This class is the request body for the quiz submission endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionDto {

    /**
     * The unique identifier of the quiz attempt being submitted.
     * This ID is obtained from the "start quiz" endpoint.
     * It must be provided to link the submission to the correct attempt record.
     */
//    @NotNull(message = "quizAttemptId is required.")
//    private Long quizAttemptId;

    @NotNull
    private LocalDateTime startedAt;
    /**
     * A list of the user's answers.
     * The list cannot be empty.
     */
    @NotEmpty(message = "Answers list cannot be empty.")
    @Valid // This annotation is crucial to trigger validation on the nested Answer objects.
    private List<Answer> answers;

    /**
     * A nested static DTO representing a single answer submitted by the user.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Answer {

        /**
         * The ID of the question being answered.
         */
        @NotNull(message = "questionId is required for each answer.")
        private Long questionId;

        /**
         * The option chosen by the user (e.g., 'A', 'B', 'C', or 'D').
         * This is represented as a char.
         */
        @NotNull(message = "submittedOption is required for each answer.")
        private Character submittedOption;
    }
}