package com.skillassessment.platform.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A custom exception thrown when a specific resource is looked up but not found.
 *
 * This provides a more semantic and specific error than a generic
 * {@link RuntimeException}. The {@link ResponseStatus} annotation ensures
 * that if this exception is unhandled by a global handler, it will still

 * result in a 404 Not Found HTTP response.
 */
@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * The name of the resource that was not found (e.g., "User", "Quiz").
     */
    private final String resourceName;

    /**
     * The name of the field that was used for the lookup (e.g., "id", "username").
     */
    private final String fieldName;

    /**
     * The value of the field that was used for the lookup.
     */
    private final Object fieldValue;

    /**
     * Constructs a new ResourceNotFoundException with a formatted message.
     *
     * @param resourceName The name of the resource (e.g., "User").
     * @param fieldName The name of the field (e.g., "id").
     * @param fieldValue The value of the field (e.g., 123).
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}