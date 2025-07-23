package com.skillassessment.platform.repository;

import com.skillassessment.platform.entity.Quiz;
import com.skillassessment.platform.enums.DifficultyLevel;
import com.skillassessment.platform.enums.ItProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Quiz} entity.
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    /**
     * Finds a single quiz by its ID and eagerly fetches its associated questions.
     * This helps prevent the N+1 query problem when accessing the questions of a quiz.
     *
     * @param id The ID of the quiz to find.
     * @return An Optional containing the Quiz with its questions, or empty if not found.
     */
    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :id")
    Optional<Quiz> findByIdWithQuestions(@Param("id") Long id);

    /**
     * Finds all quizzes that match a specific IT profile and difficulty level.
     * Used for user-facing quiz filtering.
     *
     * @param itProfile The IT profile to filter by.
     * @param difficultyLevel The difficulty level to filter by.
     * @return A list of matching quizzes.
     */
    List<Quiz> findByItProfileAndDifficultyLevel(ItProfile itProfile, DifficultyLevel difficultyLevel);

    /**
     * Finds all quizzes matching a specific IT profile.
     *
     * @param itProfile The IT profile to filter by.
     * @return A list of matching quizzes.
     */
    List<Quiz> findByItProfile(ItProfile itProfile);

    /**
     * Finds all quizzes matching a specific difficulty level.
     *
     * @param difficultyLevel The difficulty level to filter by.
     * @return A list of matching quizzes.
     */
    List<Quiz> findByDifficultyLevel(DifficultyLevel difficultyLevel);

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions")
    List<Quiz> findAllWithQuestions();
}