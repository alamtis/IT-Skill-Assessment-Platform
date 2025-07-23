package com.skillassessment.platform.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * A custom exception class used throughout the application to represent
 * specific business logic errors or API-related issues.
 *
 * This exception allows for associating a specific {@link HttpStatus} with
 * an error, which can then be used by the {@link GlobalExceptionHandler}
 * to generate the appropriate HTTP response.
 */
@Getter
public class ApiException extends RuntimeException {

    /**
     * The HTTP status code to be returned to the client for this exception.
     */
    private final HttpStatus status;

    /**
     * Constructs a new ApiException with a default status of INTERNAL_SERVER_ERROR (500).
     *
     * @param message The detail message, which is saved for later retrieval by the getMessage() method.
     */
    public ApiException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * Constructs a new ApiException with a specified HTTP status code and message.
     * This is the preferred constructor for creating exceptions related to specific
     * client errors (e.g., 400 Bad Request, 403 Forbidden, 409 Conflict).
     *
     * @param status  The HTTP status code to associate with this exception.
     * @param message The detail message.
     */
    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}