package com.skillassessment.platform.dto.ai.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import java.util.List;

@Data
public class Report {
    @JsonPropertyDescription("A concise, overall summary of the user's performance (e.g., 'Needs Improvement', 'Good Understanding').")
    @JsonProperty(required = true)
    private String overallPerformance;

    @JsonPropertyDescription("A one-sentence summary addressed to the user. Example: 'Admin, you demonstrated a foundational understanding but struggled with...'")
    @JsonProperty(required = true)
    private String summary;

    @JsonPropertyDescription("A detailed, topic-by-topic analysis of the user's incorrect answers.")
    @JsonProperty(required = true)
    private List<AnalysisArea> detailedAnalysis;
}