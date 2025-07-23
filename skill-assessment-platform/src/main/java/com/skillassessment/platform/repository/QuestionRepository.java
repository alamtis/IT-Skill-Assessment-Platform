package com.skillassessment.platform.repository;

import com.skillassessment.platform.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Question} entity.
 * Most operations on questions are cascaded via the Quiz entity, but this
 * repository is available for direct question queries if needed in the future.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Modifying
    @Query("DELETE FROM Question q WHERE q.quiz.id = :quizId")
    void deleteAllByQuizId(@Param("quizId") Long quizId);
}