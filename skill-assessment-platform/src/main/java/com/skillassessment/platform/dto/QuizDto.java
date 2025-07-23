package com.skillassessment.platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skillassessment.platform.enums.DifficultyLevel;
import com.skillassessment.platform.enums.ItProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Data Transfer Object for representing a Quiz.
 * This DTO is versatile and can represent a quiz with or without its full list of questions,
 * making it suitable for both summary views and detailed views.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON responses
public class QuizDto {

    /**
     * The unique identifier of the quiz.
     */
    private Long id;

    /**
     * The title of the quiz.
     */
    private String title;

    /**
     * A brief description of the quiz's content.
     */
    private String description;

    /**
     * The difficulty level of the quiz.
     */
    private DifficultyLevel difficultyLevel;

    /**
     * The IT profile or technology domain of the quiz.
     */
    private ItProfile itProfile;

    /**
     * A list of questions associated with this quiz.
     * This field is populated for detailed views (e.g., for an admin editing a quiz)
     * and is omitted (null) for summary views (e.g., a user browsing available quizzes).
     * The @JsonInclude(JsonInclude.Include.NON_NULL) annotation will prevent it from
     * appearing in the JSON output if it is null.
     */
    private List<QuestionDto> questions;
    private Integer numberOfQuestions;

}