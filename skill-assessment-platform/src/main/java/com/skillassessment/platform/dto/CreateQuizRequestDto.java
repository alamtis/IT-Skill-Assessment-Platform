package com.skillassessment.platform.dto;

import com.skillassessment.platform.enums.DifficultyLevel;
import com.skillassessment.platform.enums.ItProfile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for creating or updating a quiz.
 * This DTO captures the core metadata of a quiz, such as its title and category.
 * It's used as the request body for creating a quiz manually and for updating quiz details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuizRequestDto {

    /**
     * The title of the quiz.
     * Cannot be blank and must be between 5 and 100 characters long.
     */
    @NotBlank(message = "Title is mandatory and cannot be blank.")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters.")
    private String title;

    /**
     * A brief description of the quiz's content and purpose.
     * This field is optional.
     */
    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;

    /**
     * The difficulty level of the quiz (e.g., BEGINNER, INTERMEDIATE).
     * This field is mandatory.
     */
    @NotNull(message = "Difficulty level cannot be null.")
    private DifficultyLevel difficultyLevel;

    /**
     * The IT profile or technology domain this quiz is for.
     * This field is mandatory.
     */
    @NotNull(message = "IT profile cannot be null.")
    private ItProfile itProfile;
}