package com.skillassessment.platform.enums;

/**
 * Represents the difficulty level of a quiz.
 * This enum is used in the Quiz entity and various DTOs to standardize
 * difficulty representation across the application.
 */
public enum DifficultyLevel {
    /**
     * For questions and quizzes aimed at newcomers or those with basic knowledge.
     */
    BEGINNER,

    /**
     * For questions and quizzes that require a solid understanding and some practical experience.
     */
    INTERMEDIATE,

    /**
     * For questions and quizzes designed to challenge experienced professionals with deep knowledge.
     */
    ADVANCED
}