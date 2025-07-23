package com.skillassessment.platform.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserAnalyticsDto {
    private Long userId;
    private String username;
    private String email;
    private List<QuizAttemptSummaryDto> attempts;

    @Data
    @Builder
    public static class QuizAttemptSummaryDto {
        private Long attemptId;
        private String quizTitle;
        private Double percentage;
        private LocalDateTime completedAt;
    }
}