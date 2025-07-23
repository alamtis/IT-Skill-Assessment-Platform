package com.skillassessment.platform.util;

public class JsonParser {

    /**
     * Extracts a JSON object or array from a raw string response from an AI model.
     * LLMs often wrap their JSON output in markdown backticks (```json ... ```) or
     * add conversational text before or after the JSON. This utility cleans it up.
     *
     * @param rawResponse The raw string returned by the AI.
     * @return A substring that is likely to be valid JSON.
     */
    public static String extractJson(String rawResponse) {
        if (rawResponse == null || rawResponse.trim().isEmpty()) {
            return "{}"; // Return empty object if response is null or empty
        }

        // Find the first occurrence of '{' or '['
        int firstBrace = rawResponse.indexOf('{');
        int firstBracket = rawResponse.indexOf('[');

        int startIndex;

        // Determine the starting index of the JSON content
        if (firstBrace == -1) {
            startIndex = firstBracket;
        } else if (firstBracket == -1) {
            startIndex = firstBrace;
        } else {
            // If both are present, take the one that appears first
            startIndex = Math.min(firstBrace, firstBracket);
        }

        if (startIndex == -1) {
            // If neither '{' nor '[' is found, we cannot parse it as JSON
            return "{}";
        }

        // Find the last occurrence of '}' or ']'
        int lastBrace = rawResponse.lastIndexOf('}');
        int lastBracket = rawResponse.lastIndexOf(']');

        int endIndex = Math.max(lastBrace, lastBracket);

        if (endIndex == -1) {
            return "{}";
        }

        // Return the substring from the start to the end index
        return rawResponse.substring(startIndex, endIndex + 1);
    }
}