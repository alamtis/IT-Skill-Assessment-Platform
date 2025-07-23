package com.skillassessment.platform.enums;

/**
 * Represents the IT job profile or technology domain a quiz is designed for.
 * This helps in categorizing and filtering quizzes.
 * This enum can be extended to include more profiles as the platform grows.
 */
public enum ItProfile {
    // Core Development Roles
    JAVA_DEVELOPER,
    PYTHON_DEVELOPER,
    DOTNET_DEVELOPER,
    FRONTEND_ENGINEER,
    BACKEND_ENGINEER,
    FULLSTACK_DEVELOPER,
    MOBILE_DEVELOPER,

    // Operations and Infrastructure Roles
    DEVOPS_ENGINEER,
    CLOUD_ENGINEER,
    SITE_RELIABILITY_ENGINEER,

    // Data-focused Roles
    DATA_SCIENTIST,
    DATA_ANALYST,
    DATA_ENGINEER,
    MACHINE_LEARNING_ENGINEER,

    // Quality and Testing Roles
    QA_ENGINEER,
    AUTOMATION_TESTER,

    // Other Specialized Roles
    SECURITY_ANALYST,
    UI_UX_DESIGNER,
    DATABASE_ADMINISTRATOR
}