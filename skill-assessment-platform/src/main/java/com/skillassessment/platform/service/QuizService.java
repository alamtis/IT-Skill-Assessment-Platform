package com.skillassessment.platform.service;

import com.skillassessment.platform.dto.AiGenerateQuizRequestDto;
import com.skillassessment.platform.dto.CreateQuizRequestDto;
import com.skillassessment.platform.dto.QuestionDto;
import com.skillassessment.platform.dto.QuizDto;
import com.skillassessment.platform.dto.ai.response.AiQuizResponse;
import com.skillassessment.platform.entity.Question;
import com.skillassessment.platform.entity.Quiz;
import com.skillassessment.platform.enums.DifficultyLevel;
import com.skillassessment.platform.enums.ItProfile;
import com.skillassessment.platform.exception.ApiException;
import com.skillassessment.platform.exception.ResourceNotFoundException;
import com.skillassessment.platform.mapper.QuizMapper;
import com.skillassessment.platform.repository.AttemptAnswerRepository;
import com.skillassessment.platform.repository.QuestionRepository;
import com.skillassessment.platform.repository.QuizAttemptRepository;
import com.skillassessment.platform.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AiService aiService;
    private final QuizMapper quizMapper;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    @Transactional
    public QuizDto createQuiz(CreateQuizRequestDto requestDto) {
        Quiz quiz = new Quiz();
        quiz.setTitle(requestDto.getTitle());
        quiz.setDescription(requestDto.getDescription());
        quiz.setDifficultyLevel(requestDto.getDifficultyLevel());
        quiz.setItProfile(requestDto.getItProfile());

        Quiz savedQuiz = quizRepository.save(quiz);
        log.info("Manually created new quiz with ID: {}", savedQuiz.getId());
        return quizMapper.toQuizDto(savedQuiz);
    }

    @Transactional
    public QuizDto createQuizWithAi(AiGenerateQuizRequestDto requestDto) {
        log.info("Attempting AI quiz generation for profile: {}, level: {}", requestDto.getItProfile(), requestDto.getDifficultyLevel());
        AiQuizResponse aiResponse = aiService.generateQuiz(
                requestDto.getItProfile().name(), requestDto.getDifficultyLevel().name(), requestDto.getNumberOfQuestions()
        );

        if (aiResponse == null || aiResponse.getQuestions() == null || aiResponse.getQuestions().isEmpty()) {
            throw new ApiException("AI service failed to generate a valid quiz structure. Please try again.");
        }

        Quiz quiz = new Quiz();
        String quizTitle = (aiResponse.getTitle() != null && !aiResponse.getTitle().isBlank())
                ? aiResponse.getTitle()
                : String.format("%s Quiz (%s)", requestDto.getItProfile().toString().replace("_", " "), requestDto.getDifficultyLevel());
        quiz.setTitle(quizTitle);
        quiz.setDescription(String.format("An AI-generated quiz to assess skills in %s.", requestDto.getItProfile().toString().replace("_", " ")));
        quiz.setItProfile(requestDto.getItProfile());
        quiz.setDifficultyLevel(requestDto.getDifficultyLevel());

        aiResponse.getQuestions().stream()
                .map(quizMapper::toQuestionEntity)
                .forEach(quiz::addQuestion);

        Quiz savedQuiz = quizRepository.save(quiz);
        log.info("Successfully generated and saved AI quiz with ID: {}", savedQuiz.getId());
        return quizMapper.toQuizDtoWithQuestions(savedQuiz);
    }

    @Transactional
    public QuizDto addQuestionToQuiz(Long quizId, QuestionDto questionDto) {
        Quiz quiz = findQuizById(quizId);
        Question newQuestion = quizMapper.toQuestionEntity(questionDto);
        quiz.addQuestion(newQuestion);

        quizRepository.save(quiz);
        log.info("Added new question to quiz with ID: {}", quizId);
        return quizMapper.toQuizDtoWithQuestions(quiz);
    }

    @Transactional(readOnly = true)
    public List<QuizDto> getAllQuizzes() {
        List<Quiz> quizzesWithQuestions = quizRepository.findAllWithQuestions();

        return quizzesWithQuestions.stream()
                .map(quizMapper::toQuizDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuizDto getQuizById(Long id) {
        Quiz quiz = findQuizByIdWithQuestions(id);
        return quizMapper.toQuizDtoWithQuestions(quiz);
    }

    @Transactional
    public QuizDto updateQuiz(Long id, CreateQuizRequestDto requestDto) {
        Quiz quiz = findQuizById(id);
        quiz.setTitle(requestDto.getTitle());
        quiz.setDescription(requestDto.getDescription());
        quiz.setDifficultyLevel(requestDto.getDifficultyLevel());
        quiz.setItProfile(requestDto.getItProfile());

        Quiz updatedQuiz = quizRepository.save(quiz);
        log.info("Updated quiz with ID: {}", id);
        return quizMapper.toQuizDto(updatedQuiz);
    }

    @Transactional
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new ResourceNotFoundException("Quiz", "id", id);
        }

        log.info("Deleting all attempt answers for quiz ID: {}", id);
        attemptAnswerRepository.deleteAllByQuizId(id);

        log.info("Deleting all quiz attempts for quiz ID: {}", id);
        quizAttemptRepository.deleteAllByQuizId(id);

        log.info("Deleting all questions for quiz ID: {}", id);
        questionRepository.deleteAllByQuizId(id);

        log.info("Deleting quiz with ID: {}", id);
        quizRepository.deleteById(id);

        log.info("Successfully hard-deleted quiz with ID: {} and all its associated data.", id);
    }

    @Transactional
    public QuestionDto updateQuestion(Long quizId, Long questionId, QuestionDto questionDto) {
        Question question = findQuestionByIdAndQuizId(questionId, quizId);
        quizMapper.updateQuestionFromDto(question, questionDto);

        Question updatedQuestion = questionRepository.save(question);
        log.info("Updated question with ID: {} in quiz ID: {}", questionId, quizId);
        return quizMapper.toQuestionDto(updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(Long quizId, Long questionId) {
        Question question = findQuestionByIdAndQuizId(questionId, quizId);
        question.setDeleted(true);
        questionRepository.save(question); // Just save the updated state
        log.info("Soft-deleted question with ID: {} from quiz ID: {}", questionId, quizId);
    }

    @Transactional(readOnly = true)
    public List<QuizDto> getAvailableQuizzes(ItProfile itProfile, DifficultyLevel difficultyLevel) {
        List<Quiz> quizzes;
        if (itProfile != null && difficultyLevel != null) {
            quizzes = quizRepository.findByItProfileAndDifficultyLevel(itProfile, difficultyLevel);
        } else if (itProfile != null) {
            quizzes = quizRepository.findByItProfile(itProfile);
        } else if (difficultyLevel != null) {
            quizzes = quizRepository.findByDifficultyLevel(difficultyLevel);
        } else {
            quizzes = quizRepository.findAll();
        }
        return quizzes.stream()
                .map(quizMapper::toQuizDto)
                .collect(Collectors.toList());
    }

    // ===================================================================================
    // == Private Helper Methods for Internal Use
    // ===================================================================================

    private Quiz findQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));
    }

    private Quiz findQuizByIdWithQuestions(Long quizId) {
        return quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));
    }

    private Question findQuestionByIdAndQuizId(Long questionId, Long quizId) {
        findQuizById(quizId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        if (!question.getQuiz().getId().equals(quizId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Question with id " + questionId + " does not belong to quiz with id " + quizId);
        }
        return question;
    }
}