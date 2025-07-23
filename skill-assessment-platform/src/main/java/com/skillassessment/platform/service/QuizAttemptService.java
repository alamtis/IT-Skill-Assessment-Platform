package com.skillassessment.platform.service;

import com.skillassessment.platform.dto.QuizAttemptDto;
import com.skillassessment.platform.dto.QuizHistoryDto;
import com.skillassessment.platform.dto.QuizSubmissionDto;
import com.skillassessment.platform.dto.QuizSubmissionResponseDto;
import com.skillassessment.platform.entity.*;
import com.skillassessment.platform.enums.RoleName;
import com.skillassessment.platform.exception.ApiException;
import com.skillassessment.platform.exception.ResourceNotFoundException;
import com.skillassessment.platform.repository.QuestionRepository;
import com.skillassessment.platform.repository.QuizAttemptRepository;
import com.skillassessment.platform.repository.QuizRepository;
import com.skillassessment.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service layer for handling the entire lifecycle of a user's quiz attempt.
 * This includes starting, submitting, scoring, and retrieving attempt history.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AiService aiService;

    /**
     * Records the start of a user's quiz attempt.
     *
     * @param quizId The ID of the quiz being started.
     * @return The unique ID of the newly created quiz attempt.
     */
    @Transactional
    public Long startQuiz(Long quizId) {
        User user = getCurrentUser();
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        QuizAttempt attempt = new QuizAttempt();
        attempt.setAttemptAnswers(new ArrayList<>());
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setStartedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        log.info("User '{}' started quiz '{}'. Attempt ID: {}", user.getUsername(), quiz.getTitle(), savedAttempt.getId());
        return savedAttempt.getId();
    }

    /**
     * Processes a user's submission for a quiz, calculates the score,
     * triggers AI report generation, and persists the results.
     *
     * @param quizId The ID of the quiz being submitted.
     * @param submissionDto The DTO containing the attempt ID and answers.
     * @return A DTO with the final score, a link to the report, and the study plan.
     */
    @Transactional
    public QuizSubmissionResponseDto submitQuiz(Long quizId, QuizSubmissionDto submissionDto) {
        log.info("Processing new quiz submission for quiz ID: {}", quizId);

        // --- 1. Fetch User and Quiz Data ---
        User user = getCurrentUser();
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setStartedAt(submissionDto.getStartedAt());
        attempt.setCompletedAt(LocalDateTime.now());
        attempt.setAttemptAnswers(new ArrayList<>());

        // --- 3. Process Answers and Calculate Score ---
        List<Long> questionIds = submissionDto.getAnswers().stream().map(QuizSubmissionDto.Answer::getQuestionId).toList();
        Map<Long, Question> questionsMap = quiz.getQuestions().stream()
                .filter(q -> questionIds.contains(q.getId())) // Ensure we only use questions from this quiz
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        int correctAnswersCount = 0;

        for (QuizSubmissionDto.Answer submittedAnswer : submissionDto.getAnswers()) {
            Question question = questionsMap.get(submittedAnswer.getQuestionId());
            if (question == null) {
                log.warn("Submission for quiz {} contained a question ID ({}) that does not belong to this quiz. Skipping.", quizId, submittedAnswer.getQuestionId());
                continue;
            }

            boolean isCorrect = Character.toUpperCase(question.getCorrectOption()) == Character.toUpperCase(submittedAnswer.getSubmittedOption());
            if (isCorrect) {
                correctAnswersCount++;
            }

            AttemptAnswer answerRecord = new AttemptAnswer();
            answerRecord.setQuizAttempt(attempt); // Link to the new parent attempt
            answerRecord.setQuestion(question);
            answerRecord.setSubmittedOption(Character.toUpperCase(submittedAnswer.getSubmittedOption()));
            answerRecord.setIsCorrect(isCorrect);

            attempt.getAttemptAnswers().add(answerRecord); // Add to the managed collection
        }

        // --- 4. Finalize Attempt Details ---
        int totalQuestions = questionsMap.size();
        double score = (double) correctAnswersCount;
        double percentage = (totalQuestions > 0) ? (score / totalQuestions) * 100 : 0;

        attempt.setScore(score);
        attempt.setPercentage(percentage);
        attempt.setTimeTakenSeconds(Duration.between(attempt.getStartedAt(), attempt.getCompletedAt()).getSeconds());

        // --- 5. AI Report Generation ---
        try {
            Map<String, String> aiGeneratedContent = aiService.generateReportAndStudyPlan(user, quiz, attempt.getAttemptAnswers());
            attempt.setDetailedReportJson(aiGeneratedContent.get("report"));
            attempt.setStudyPlanJson(aiGeneratedContent.get("studyPlan"));
            log.info("Successfully generated AI report for user '{}' on quiz '{}'.", user.getUsername(), quiz.getTitle());
        } catch (Exception e) {
            log.error("Failed to generate AI report for user '{}' on quiz '{}': {}", user.getUsername(), quiz.getTitle(), e.getMessage());
            attempt.setDetailedReportJson("{\"error\":\"AI report generation failed. Please contact support.\"}");
            attempt.setStudyPlanJson("{\"error\":\"AI study plan generation failed.\"}");
        }

        // --- 6. Save the Complete Attempt Record ---
        // This single save operation will cascade and insert the QuizAttempt and all its associated AttemptAnswer records.
        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        log.info("Saved new quiz attempt with ID: {} for user '{}'.", savedAttempt.getId(), user.getUsername());

        // --- 7. Return the Response ---
        return QuizSubmissionResponseDto.builder()
                .attemptId(savedAttempt.getId())
                .score(score)
                .totalQuestions((double) totalQuestions)
                .percentage(percentage)
                .reportUrl("/api/reports/" + savedAttempt.getId())
                .studyPlan(attempt.getStudyPlanJson())
                .build();
    }

    /**
     * Retrieves the quiz history for a specific user.
     * Enforces security rules: users can see their own history, admins can see any user's history.
     */
    @Transactional(readOnly = true)
    public List<QuizHistoryDto> getQuizHistoryForUser(Long userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) && currentUser.getRoles().stream().noneMatch(r -> r.getName() == RoleName.ROLE_ADMIN)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You do not have permission to view this user's history.");
        }

        List<QuizAttempt> attempts = quizAttemptRepository.findByUserIdOrderByCompletedAtDesc(userId);
        return attempts.stream()
                .map(this::mapToQuizHistoryDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a detailed report for a single quiz attempt.
     */
    @Transactional(readOnly = true)
    public QuizAttemptDto getDetailedReport(Long quizAttemptId) {
        QuizAttempt attempt = quizAttemptRepository.findByIdWithDetails(quizAttemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt", "id", quizAttemptId));

        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(attempt.getUser().getId()) && currentUser.getRoles().stream().noneMatch(r -> r.getName() == RoleName.ROLE_ADMIN)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You do not have permission to view this report.");
        }

        return mapToQuizAttemptDto(attempt);
    }

    // ===================================================================================
    // == Private Helper Methods
    // ===================================================================================

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private void validateSubmission(QuizAttempt attempt, User user, Long quizId) {
        if (!attempt.getUser().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You cannot submit an attempt that does not belong to you.");
        }
        if (!attempt.getQuiz().getId().equals(quizId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Submission attempt ID does not match the quiz ID from the URL.");
        }
        if (attempt.getCompletedAt() != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "This quiz has already been submitted and cannot be changed.");
        }
    }

    private QuizHistoryDto mapToQuizHistoryDto(QuizAttempt attempt) {
        return QuizHistoryDto.builder()
                .quizAttemptId(attempt.getId())
                .quizId(attempt.getQuiz().getId())
                .quizTitle(attempt.getQuiz().getTitle())
                .score(attempt.getScore())
                .percentage(attempt.getPercentage())
                .completedAt(attempt.getCompletedAt())
                .build();
    }

    private QuizAttemptDto mapToQuizAttemptDto(QuizAttempt attempt) {
        List<QuizAttemptDto.AttemptAnswerDto> answerDtos = attempt.getAttemptAnswers() == null ? new ArrayList<>() :
                attempt.getAttemptAnswers().stream()
                        .map(this::mapToAttemptAnswerDto)
                        .collect(Collectors.toList());

        return QuizAttemptDto.builder()
                .id(attempt.getId())
                .userId(attempt.getUser().getId())
                .quizId(attempt.getQuiz().getId())
                .score(attempt.getScore())
                .percentage(attempt.getPercentage())
                .startedAt(attempt.getStartedAt())
                .completedAt(attempt.getCompletedAt())
                .timeTakenSeconds(attempt.getTimeTakenSeconds())
                .detailedReportJson(attempt.getDetailedReportJson())
                .studyPlanJson(attempt.getStudyPlanJson())
                .answers(answerDtos)
                .build();
    }

    private QuizAttemptDto.AttemptAnswerDto mapToAttemptAnswerDto(AttemptAnswer attemptAnswer) {
        return QuizAttemptDto.AttemptAnswerDto.builder()
                .questionId(attemptAnswer.getQuestion().getId())
                .questionText(attemptAnswer.getQuestion().getQuestionText())
                .submittedOption(attemptAnswer.getSubmittedOption())
                .correctOption(attemptAnswer.getQuestion().getCorrectOption())
                .isCorrect(attemptAnswer.getIsCorrect())
                .explanation(attemptAnswer.getQuestion().getExplanation())
                .build();
    }
    /**
     * (Admin only) Deletes a specific quiz attempt record.
     * This will also cascade and delete all associated attempt_answers.
     * @param attemptId The ID of the quiz attempt to delete.
     */
    @Transactional
    public void deleteQuizAttempt(Long attemptId) {
        if (!quizAttemptRepository.existsById(attemptId)) {
            // Throw an exception if the attempt doesn't exist to give clear feedback
            throw new ResourceNotFoundException("QuizAttempt", "id", attemptId);
        }
        quizAttemptRepository.deleteById(attemptId);
        log.info("Admin deleted quiz attempt with ID: {}", attemptId);
    }
}