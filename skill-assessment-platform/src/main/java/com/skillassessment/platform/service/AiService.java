package com.skillassessment.platform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillassessment.platform.dto.ai.GeminiRequest;
import com.skillassessment.platform.dto.ai.GeminiResponse;
import com.skillassessment.platform.dto.ai.response.AiQuizResponse;
import com.skillassessment.platform.entity.AttemptAnswer;
import com.skillassessment.platform.entity.Quiz;
import com.skillassessment.platform.entity.User;
import com.skillassessment.platform.exception.ApiException;
import com.skillassessment.platform.util.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String modelName;
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    public AiService(WebClient.Builder webClientBuilder,
                     ObjectMapper objectMapper,
                     @Value("${gemini.api.key}") String apiKey,
                     @Value("${gemini.model.name}") String modelName) {
        this.webClient = webClientBuilder.baseUrl(GEMINI_API_URL).build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.modelName = modelName;
    }

    private String callGeminiApi(String prompt) {
        GeminiRequest requestPayload = GeminiRequest.fromPrompt(prompt);

        try {
            GeminiResponse response = this.webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path(this.modelName + ":generateContent")
                            .queryParam("key", this.apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(requestPayload), GeminiRequest.class)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();

            if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty() ||
                    response.getCandidates().get(0).getContent() == null ||
                    response.getCandidates().get(0).getContent().getParts() == null ||
                    response.getCandidates().get(0).getContent().getParts().isEmpty()) {
                throw new ApiException("AI service returned an empty or invalid response structure.");
            }

            return response.getCandidates().get(0).getContent().getParts().get(0).getText();
        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage(), e);
            throw new ApiException("Failed to communicate with the AI service. " + e.getMessage());
        }
    }

    /**
     * Generates a complete quiz structure (title and questions) using the AI.
     *
     * @param itProfile         The IT profile for the quiz.
     * @param difficultyLevel   The difficulty level.
     * @param numberOfQuestions The number of questions to generate.
     * @return An AiQuizResponse object containing the generated title and questions.
     */
    public AiQuizResponse generateQuiz(String itProfile, String difficultyLevel, int numberOfQuestions) {
        // The new prompt asks for a root-level object with 'title' and 'questions' keys
        String prompt = String.format(
                "Generate a complete IT skill assessment quiz. The output MUST be a single, valid JSON object with no surrounding text or markdown. " +
                        "The JSON object must have two top-level keys: 'title' (a short, relevant string for the quiz title, e.g., 'Intermediate Python Challenge') and 'questions' (a JSON array). " +
                        "Generate %d multiple-choice questions for the topic of '%s' at a '%s' difficulty level. " +
                        "Each question object in the 'questions' array must have the following exact structure: " +
                        "{\"questionText\": \"...\", \"optionA\": \"...\", \"optionB\": \"...\", \"optionC\": \"...\", \"optionD\": \"...\", \"correctOption\": \"A\", \"explanation\": \"...\"}. " +
                        "Example output format: {\"title\": \"Beginner Java Concepts\", \"questions\": [{\"questionText\": \"...\", ...}]}",
                numberOfQuestions, itProfile, difficultyLevel);

        log.info("Sending structured prompt to Free Gemini API for full quiz generation...");
        String jsonResponse = callGeminiApi(prompt);
        String cleanedJson = JsonParser.extractJson(jsonResponse);

        try {
            // Deserialize the response into our new AiQuizResponse DTO
            return objectMapper.readValue(cleanedJson, AiQuizResponse.class);
        } catch (Exception e) {
            log.error("Failed to parse structured JSON quiz from Gemini API. Raw response: {}", cleanedJson);
            throw new ApiException("AI service returned a malformed JSON for the quiz.");
        }
    }

    public Map<String, String> generateReportAndStudyPlan(User user, Quiz quiz, List<AttemptAnswer> answers) {
        String incorrectAnswersSummary = answers.stream()
                .filter(answer -> !answer.getIsCorrect())
                .map(answer -> String.format(
                        "Question: '%s'", answer.getQuestion().getQuestionText()
                ))
                .collect(Collectors.joining("\n- ", "\n- ", ""));

        if (incorrectAnswersSummary.trim().equals("-")) {
            // Handle the "perfect score" case
            return Map.of(
                    "report", "{\"overallPerformance\":\"Excellent\",\"summary\":\"Congratulations! You answered all questions correctly.\",\"detailedAnalysis\":[]}",
                    "studyPlan", "{\"focusAreas\":[\"Advanced Topics\"],\"suggestedSchedule\":[{\"title\":\"Next Steps\",\"topic\":\"Advanced Concepts\",\"activities\":[\"Explore more advanced topics in this area.\"],\"timeCommitment\":\"Ongoing\"}]}"
            );
        }

        // 1. Define the exact JSON structure we want as an example.
        String jsonExample = """
                {
                  "report": {
                    "overallPerformance": "Good Understanding",
                    "summary": "Jane, you have a solid grasp of the basics but should review...",
                    "detailedAnalysis": [
                      {
                        "topic": "Variable Fundamentals",
                        "explanation": "Your mistake on the variable question suggests...",
                        "recommendedResources": ["https://docs.python.org/3/tutorial/introduction.html#first-steps-towards-programming"]
                      }
                    ]
                  },
                  "studyPlan": {
                    "focusAreas": ["Python Data Types", "Conditional Logic"],
                    "suggestedSchedule": [
                      {
                        "title": "Day 1",
                        "topic": "Integers and Floats",
                        "activities": ["Read the official documentation...", "Complete 5 exercises..."],
                        "timeCommitment": "1 hour"
                      }
                    ]
                  }
                }
                """;

        String userName = user.getUsername();
        String prompt = String.format(
                "TASK: Analyze a user's quiz results and generate a JSON object containing a detailed report and a study plan. " +
                        "You MUST follow the JSON structure provided in the example below EXACTLY. Do not add or remove keys. " +
                        "Address the user '%s' by name in the summary. " +
                        "The final output MUST be only the raw JSON object without any surrounding text, comments, or markdown backticks (`). " +

                        "--- EXAMPLE OF PERFECT OUTPUT STRUCTURE ---" +
                        "%s" + // Inserting the jsonExample here
                        "--- END OF EXAMPLE ---" +

                        "--- ACTUAL TASK ---" +
                        "USER NAME: %s" +
                        "QUIZ TOPIC: %s" +
                        "DIFFICULTY: %s" +
                        "INCORRECT QUESTIONS:" +
                        "%s" +
                        "--- GENERATE JSON OUTPUT NOW ---",
                userName,
                jsonExample,
                userName,
                quiz.getItProfile().toString().replace("_", " "),
                quiz.getDifficultyLevel(),
                incorrectAnswersSummary
        );

        log.info("Sending structured few-shot prompt to Gemini Developer API...");
        String jsonResponse = callGeminiApi(prompt);
        String cleanedJson = JsonParser.extractJson(jsonResponse);

        try {
            // 1. Parse the entire AI response into a generic Map structure.
            Map<String, Object> fullResponse = objectMapper.readValue(cleanedJson, new TypeReference<>() {
            });

            // 2. Extract the 'report' and 'studyPlan' parts.
            Object reportObject = fullResponse.get("report");
            Object studyPlanObject = fullResponse.get("studyPlan");

            // 3. Convert these parts back into clean JSON strings.
            String reportJson = objectMapper.writeValueAsString(reportObject);
            String studyPlanJson = objectMapper.writeValueAsString(studyPlanObject);

            // 4. Return the map of JSON strings.
            return Map.of("report", reportJson, "studyPlan", studyPlanJson);
        } catch (Exception e) {
            log.error("Failed to parse JSON from Gemini API for report. Raw response: {}", cleanedJson);
            throw new ApiException("AI service returned a malformed JSON for the report.");
        }
    }
}