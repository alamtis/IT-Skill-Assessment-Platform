package com.skillassessment.platform.dto.ai.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import java.util.List;

@Data
public class ScheduleItem {
    @JsonPropertyDescription("The title for this part of the schedule (e.g., 'Day 1', 'Week 1').")
    @JsonProperty(required = true)
    private String title;

    @JsonPropertyDescription("The specific topic to be studied during this period.")
    @JsonProperty(required = true)
    private String topic;

    @JsonPropertyDescription("A list of concrete, actionable tasks for the user to complete.")
    @JsonProperty(required = true)
    private List<String> activities;

    @JsonPropertyDescription("An estimated time commitment for this schedule item (e.g., '2 hours').")
    @JsonProperty(required = true)
    private String timeCommitment;
}