package com.skillassessment.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A Data Transfer Object (DTO) used to provide a consistent, structured
 * error response for API clients. This class is typically used by the
 * GlobalExceptionHandler to format error details into a JSON object.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetailsDto {

    /**
     * The timestamp indicating when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * A concise, human-readable message summarizing the error.
     * This usually comes directly from the exception's message.
     */
    private String message;

    /**
     * Additional details about the error, typically the URI of the
     * request that caused the error (e.g., "uri=/api/users/999").
     */
    private String details;

}