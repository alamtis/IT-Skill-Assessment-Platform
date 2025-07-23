package com.skillassessment.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A lightweight Data Transfer Object (DTO) for representing a single entry
 * in a user's quiz attempt history. This is designed for summary views
 * and does not contain detailed report information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizHistoryDto {

    /**
     * The unique identifier of this specific quiz attempt.
     * This ID can be used to fetch the full detailed report.
     */
    private Long quizAttemptId;

    /**
     * The ID of the quiz that was taken.
     */
    private Long quizId;

    /**
     * The title of the quiz that was taken. This is included for
     * better display on the frontend without requiring an extra API call.
     */
    private String quizTitle;

    /**
     * The final score (number of correct answers) for this attempt.
     */
    private Double score;

    /**
     * The final score as a percentage.
     */
    private Double percentage;

    /**
     * The timestamp when the quiz was completed.
     */
    private LocalDateTime completedAt;
}