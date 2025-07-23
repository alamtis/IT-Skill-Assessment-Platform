package com.skillassessment.platform.dto.ai.response;

import com.skillassessment.platform.dto.QuestionDto;
import lombok.Data;

import java.util.List;

@Data
public class AiQuizResponse {
    private String title;
    private List<QuestionDto> questions;
}