package com.skillassessment.platform.controller;

import com.skillassessment.platform.dto.QuizDto;
import com.skillassessment.platform.dto.QuizSubmissionDto;
import com.skillassessment.platform.dto.QuizSubmissionResponseDto;
import com.skillassessment.platform.enums.DifficultyLevel;
import com.skillassessment.platform.enums.ItProfile;
import com.skillassessment.platform.service.QuizAttemptService;
import com.skillassessment.platform.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class UserQuizController {

    private final QuizAttemptService quizAttemptService;
    private final QuizService quizService;


    @PostMapping("/{quizId}/start")
    public ResponseEntity<Map<String, Long>> startQuiz(@PathVariable Long quizId) {
        Long quizAttemptId = quizAttemptService.startQuiz(quizId);
        return ResponseEntity.ok(Map.of("quizAttemptId", quizAttemptId));
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<QuizSubmissionResponseDto> submitQuizAnswers(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizSubmissionDto submissionDto) {
        QuizSubmissionResponseDto response = quizAttemptService.submitQuiz(quizId, submissionDto);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/quizzes : Retrieve a list of quizzes available to users (excluding questions).
     * This endpoint supports filtering by IT profile and difficulty level.
     *
     * @param itProfile Optional filter for the IT profile of the quiz (e.g., "JAVA_DEVELOPER").
     * @param difficultyLevel Optional filter for the difficulty level of the quiz (e.g., "INTERMEDIATE").
     * @return ResponseEntity with a list of QuizDto objects (without question details) and HTTP status 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<QuizDto>> getAvailableQuizzes(
            @RequestParam(required = false) ItProfile itProfile,
            @RequestParam(required = false) DifficultyLevel difficultyLevel) {
        List<QuizDto> quizzes = quizService.getAvailableQuizzes(itProfile, difficultyLevel);
        return ResponseEntity.ok(quizzes);
    }
}
