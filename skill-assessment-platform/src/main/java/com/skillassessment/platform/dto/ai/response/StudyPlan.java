package com.skillassessment.platform.dto.ai.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import java.util.List;

@Data
public class StudyPlan {
    @JsonPropertyDescription("The main areas the user should focus on.")
    @JsonProperty(required = true)
    private List<String> focusAreas;

    @JsonPropertyDescription("A structured, day-by-day or topic-by-topic schedule of learning activities.")
    @JsonProperty(required = true)
    private List<ScheduleItem> suggestedSchedule;
}