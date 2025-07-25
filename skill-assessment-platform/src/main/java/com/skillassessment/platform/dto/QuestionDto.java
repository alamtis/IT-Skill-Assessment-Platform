package com.skillassessment.platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for representing a quiz question.
 * Used for creating new questions, displaying them to admins,
 * and as a structured format for AI-generated content.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Omit null fields from JSON output
public class QuestionDto {

    /**
     * The unique identifier of the question.
     * This is generated by the database, so it's not required for creation requests
     * but is included in responses.
     */
    private Long id;

    /**
     * The text of the question itself.
     */
    @NotBlank(message = "Question text cannot be blank.")
    @Size(min = 10, max = 1000, message = "Question text must be between 10 and 1000 characters.")
    private String questionText;

    /**
     * The text for choice A.
     */
    @NotBlank(message = "Option A cannot be blank.")
    @Size(max = 255, message = "Option A cannot exceed 255 characters.")
    private String optionA;

    /**
     * The text for choice B.
     */
    @NotBlank(message = "Option B cannot be blank.")
    @Size(max = 255, message = "Option B cannot exceed 255 characters.")
    private String optionB;

    /**
     * The text for choice C.
     */
    @NotBlank(message = "Option C cannot be blank.")
    @Size(max = 255, message = "Option C cannot exceed 255 characters.")
    private String optionC;

    /**
     * The text for choice D.
     */
    @NotBlank(message = "Option D cannot be blank.")
    @Size(max = 255, message = "Option D cannot exceed 255 characters.")
    private String optionD;

    /**
     * The correct option, represented by a single character string ('A', 'B', 'C', or 'D').
     * The validation ensures it's one of these characters, case-insensitive.
     * The entity stores this as a char, but using a String in the DTO is more flexible for JSON.
     */
    @NotBlank(message = "Correct option cannot be blank.")
    @Pattern(regexp = "^[A-Da-d]$", message = "Correct option must be a single character: 'A', 'B', 'C', or 'D'.")
    private String correctOption;

    /**
     * An optional explanation for why the correct answer is right.
     * Useful for user feedback after a quiz.
     */
    @Size(max = 2000, message = "Explanation cannot exceed 2000 characters.")
    private String explanation;
}