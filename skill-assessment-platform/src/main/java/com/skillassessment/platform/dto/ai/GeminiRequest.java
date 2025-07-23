package com.skillassessment.platform.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequest {
    private List<Content> contents;

    /**
     * A convenience factory method to create the nested request structure from a simple prompt string.
     * @param prompt The text prompt to send to the AI.
     * @return A fully formed GeminiRequest object.
     */
    public static GeminiRequest fromPrompt(String prompt) {
        Part part = new Part(prompt);
        Content content = new Content(List.of(part), "user");
        return new GeminiRequest(List.of(content));
    }
}