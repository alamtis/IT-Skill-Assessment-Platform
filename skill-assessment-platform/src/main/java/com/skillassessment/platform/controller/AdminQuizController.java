package com.skillassessment.platform.controller;

import com.skillassessment.platform.dto.AiGenerateQuizRequestDto;
import com.skillassessment.platform.dto.CreateQuizRequestDto;
import com.skillassessment.platform.dto.QuestionDto;
import com.skillassessment.platform.dto.QuizDto;
import com.skillassessment.platform.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/quizzes")
@RequiredArgsConstructor
public class AdminQuizController {

    private final QuizService quizService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDto> createQuizManually(@Valid @RequestBody CreateQuizRequestDto requestDto) {
        return new ResponseEntity<>(quizService.createQuiz(requestDto), HttpStatus.CREATED);
    }

    @PostMapping("/generate-ai")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDto> generateQuizViaAi(@Valid @RequestBody AiGenerateQuizRequestDto requestDto) {
        QuizDto quiz = quizService.createQuizWithAi(requestDto);
        return new ResponseEntity<>(quiz, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<QuizDto>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    /**
     * GET /api/admin/quizzes/{id} : Get details of a specific quiz, including all its questions.
     * This is useful for admins to review a complete quiz before it's published.
     *
     * @param id The ID of the quiz to retrieve.
     * @return ResponseEntity with the QuizDto (including questions) and HTTP status 200 (OK).
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuizDto> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    /**
     * PUT /api/admin/quizzes/{id} : Update an existing quiz's details (e.g., title, description).
     * This does not modify the questions associated with the quiz.
     *
     * @param id The ID of the quiz to update.
     * @param requestDto DTO containing the new details for the quiz.
     * @return ResponseEntity with the updated QuizDto and HTTP status 200 (OK).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDto> updateQuiz(@PathVariable Long id, @Valid @RequestBody CreateQuizRequestDto requestDto) {
        return ResponseEntity.ok(quizService.updateQuiz(id, requestDto));
    }

    /**
     * DELETE /api/admin/quizzes/{id} : Delete a quiz and all its associated questions.
     * This is a permanent action.
     *
     * @param id The ID of the quiz to delete.
     * @return ResponseEntity with no content and HTTP status 204 (No Content) on successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/questions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuizDto> addQuestionToQuiz(@PathVariable Long quizId, @Valid @RequestBody QuestionDto questionDto) {
        return ResponseEntity.ok(quizService.addQuestionToQuiz(quizId, questionDto));
    }

    /**
     * PUT /api/admin/quizzes/{quizId}/questions/{questionId} : Updates an existing question.
     * @param quizId The ID of the parent quiz.
     * @param questionId The ID of the question to update.
     * @param questionDto The DTO with the updated question details.
     * @return ResponseEntity with the updated QuestionDto.
     */
    @PutMapping("/{quizId}/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuestionDto> updateQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionDto questionDto) {
        return ResponseEntity.ok(quizService.updateQuestion(quizId, questionId, questionDto));
    }

    /**
     * DELETE /api/admin/quizzes/{quizId}/questions/{questionId} : Deletes a question.
     * @param quizId The ID of the parent quiz.
     * @param questionId The ID of the question to delete.
     * @return ResponseEntity with no content.
     */
    @DeleteMapping("/{quizId}/questions/{questionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long quizId,
            @PathVariable Long questionId) {
        quizService.deleteQuestion(quizId, questionId);
        return ResponseEntity.noContent().build();
    }
}