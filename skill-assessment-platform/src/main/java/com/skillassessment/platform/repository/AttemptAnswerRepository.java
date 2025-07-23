package com.skillassessment.platform.repository;

import com.skillassessment.platform.entity.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link AttemptAnswer} entity.
 * Interactions with AttemptAnswer are typically managed via cascading operations
 * from the parent QuizAttempt entity, but this repository provides a dedicated
 * data access layer, adhering to best practices and allowing for future extensions.
 */
@Repository
public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long> {
    boolean existsByQuestionId(Long questionId);

    @Modifying
    @Query("DELETE FROM AttemptAnswer aa WHERE aa.quizAttempt.quiz.id = :quizId")
    void deleteAllByQuizId(@Param("quizId") Long quizId);
}