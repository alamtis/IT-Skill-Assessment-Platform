package com.skillassessment.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    private Double score;
    private Double percentage;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long timeTakenSeconds;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String detailedReportJson; // Storing AI-generated report

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String studyPlanJson; // Storing AI-generated study plan

    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttemptAnswer> attemptAnswers;
}