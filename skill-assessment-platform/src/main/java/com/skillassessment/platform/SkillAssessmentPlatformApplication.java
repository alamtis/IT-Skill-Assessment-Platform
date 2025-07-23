package com.skillassessment.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // This is often added in a separate config, but fine here for simplicity
public class SkillAssessmentPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkillAssessmentPlatformApplication.class, args);
    }
}