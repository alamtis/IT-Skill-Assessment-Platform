package com.skillassessment.platform.controller;

import com.skillassessment.platform.dto.QuizAttemptDto;
import com.skillassessment.platform.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for retrieving detailed quiz attempt reports.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final QuizAttemptService quizAttemptService;

    /**
     * GET /api/reports/{quizAttemptId} : Retrieves the detailed AI-generated report
     * and study plan for a specific quiz attempt.
     *
     * <p><b>Security:</b> Access to a report is strictly controlled. A user can only
     * view their own reports. An administrator (ROLE_ADMIN) has the privilege to
     * view any user's report. An attempt to access another user's report without
     * admin rights will result in a 403 Forbidden error.</p>
     *
     * @param quizAttemptId The unique identifier of the quiz attempt.
     * @return ResponseEntity containing a detailed {@link QuizAttemptDto} which includes
     *         the score, AI-generated report, study plan, and a breakdown of answers.
     */
    @GetMapping("/{quizAttemptId}")
    public ResponseEntity<QuizAttemptDto> getDetailedReport(@PathVariable Long quizAttemptId) {
        QuizAttemptDto reportDto = quizAttemptService.getDetailedReport(quizAttemptId);
        return ResponseEntity.ok(reportDto);
    }
}