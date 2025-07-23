package com.skillassessment.platform.repository;

import com.skillassessment.platform.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link QuizAttempt} entity.
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    /**
     * Finds all quiz attempts for a specific user, ordered by completion date descending.
     *
     * @param userId The ID of the user whose history is being requested.
     * @return A list of the user's quiz attempts.
     */
    List<QuizAttempt> findByUserIdOrderByCompletedAtDesc(Long userId);

    /**
     * Finds a single quiz attempt by its ID and eagerly fetches the associated quiz,
     * attempt answers, and the question for each answer.
     * This is used to build a detailed report without causing N+1 query issues.
     *
     * @param quizAttemptId The ID of the quiz attempt.
     * @return An Optional containing the fully populated QuizAttempt object for reporting.
     */
    @Query("SELECT qa FROM QuizAttempt qa " +
            "LEFT JOIN FETCH qa.quiz " +
            "LEFT JOIN FETCH qa.attemptAnswers aa " +
            "LEFT JOIN FETCH aa.question " +
            "WHERE qa.id = :quizAttemptId")
    Optional<QuizAttempt> findByIdWithDetails(@Param("quizAttemptId") Long quizAttemptId);

    @Modifying
    @Query("DELETE FROM QuizAttempt qa WHERE qa.quiz.id = :quizId")
    void deleteAllByQuizId(@Param("quizId") Long quizId);
}