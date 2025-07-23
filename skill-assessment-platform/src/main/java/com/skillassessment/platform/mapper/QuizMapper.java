package com.skillassessment.platform.mapper;

import com.skillassessment.platform.dto.QuestionDto;
import com.skillassessment.platform.dto.QuizDto;
import com.skillassessment.platform.entity.Question;
import com.skillassessment.platform.entity.Quiz;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class QuizMapper {

    public QuizDto toQuizDto(Quiz quiz) {
        QuizDto dto = new QuizDto();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setDifficultyLevel(quiz.getDifficultyLevel());
        dto.setItProfile(quiz.getItProfile());

        if (quiz.getQuestions() != null) {
            dto.setNumberOfQuestions(quiz.getQuestions().size());
        } else {
            dto.setNumberOfQuestions(0);
        }

        return dto;
    }

    public QuizDto toQuizDtoWithQuestions(Quiz quiz) {
        QuizDto dto = toQuizDto(quiz);
        if (quiz.getQuestions() != null) {
            dto.setQuestions(quiz.getQuestions().stream()
                    .map(this::toQuestionDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public QuestionDto toQuestionDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        dto.setCorrectOption(String.valueOf(question.getCorrectOption()));
        dto.setExplanation(question.getExplanation());
        return dto;
    }

    public Question toQuestionEntity(QuestionDto dto) {
        Question question = new Question();
        updateQuestionFromDto(question, dto);
        return question;
    }

    public void updateQuestionFromDto(Question question, QuestionDto dto) {
        question.setQuestionText(dto.getQuestionText());
        question.setOptionA(dto.getOptionA());
        question.setOptionB(dto.getOptionB());
        question.setOptionC(dto.getOptionC());
        question.setOptionD(dto.getOptionD());
        question.setCorrectOption(dto.getCorrectOption().charAt(0));
        question.setExplanation(dto.getExplanation());
    }
}