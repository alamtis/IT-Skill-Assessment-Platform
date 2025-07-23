package com.skillassessment.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A Data Transfer Object (DTO) that represents the immediate feedback
 * to a user after they submit a quiz. It provides the score, a link to the
 * detailed report, and the AI-generated study plan.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionResponseDto {

    private Long attemptId;

    /**
     * The raw number of questions answered correctly.
     */
    private Double score;

    /**
     * The total number of questions in the quiz, providing context for the raw score.
     */
    private Double totalQuestions;

    /**
     * The final score expressed as a percentage (e.g., 80.0 for 80%).
     */
    private Double percentage;

    /**
     * A relative URL path to the full, detailed report for this attempt.
     * The client application can use this URL to allow the user to navigate
     * to a page showing their complete results and AI analysis.
     * Example: "/api/reports/123"
     */
    private String reportUrl;

    /**
     * The AI-generated study plan, returned as a JSON string.
     * The frontend is responsible for parsing this string to display the
     * personalized study recommendations to the user.
     */
    private String studyPlan;
}