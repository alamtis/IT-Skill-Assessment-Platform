package com.skillassessment.platform.dto.ai.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import java.util.List;

@Data
public class AnalysisArea {
    @JsonPropertyDescription("The general topic of the question the user got wrong (e.g., 'Data Types', 'Conditional Statements').")
    @JsonProperty(required = true)
    private String topic;

    @JsonPropertyDescription("A detailed explanation of why the user's answer was incorrect and what the correct concept is.")
    @JsonProperty(required = true)
    private String explanation;

    @JsonPropertyDescription("A list of external URLs for articles or tutorials related to this specific topic.")
    @JsonProperty(required = true)
    private List<String> recommendedResources;
}