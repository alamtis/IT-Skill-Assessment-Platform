package com.skillassessment.platform.exception;

import com.skillassessment.platform.dto.ErrorDetailsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for the entire application.
 * This class uses @ControllerAdvice to act as a centralized point for
 * handling exceptions thrown by controllers, ensuring consistent and
 * structured JSON error responses are sent to the client.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles custom business logic exceptions (ApiException).
     * This allows us to throw exceptions with a specific HTTP status from our service layer.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorDetailsDto> handleApiException(ApiException exception, WebRequest webRequest) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(
                LocalDateTime.now(),
                exception.getMessage(),
                "uri=" + webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, exception.getStatus());
    }

    /**
     * Handles exceptions for when a requested resource is not found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetailsDto> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest webRequest) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(
                LocalDateTime.now(),
                exception.getMessage(),
                "uri=" + webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles authentication failures (e.g., incorrect username or password).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetailsDto> handleBadCredentialsException(BadCredentialsException exception, WebRequest webRequest) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(
                LocalDateTime.now(),
                "Invalid username or password", // Provide a generic message for security
                "uri=" + webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles authorization failures, where a user is authenticated but does not have
     * the necessary role to access a resource.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDetailsDto> handleAccessDeniedException(AccessDeniedException exception, WebRequest webRequest) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(
                LocalDateTime.now(),
                "Access Denied: You do not have permission to access this resource.",
                "uri=" + webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    /**
     * Overrides the default handler for validation exceptions (@Valid).
     * This provides a more structured response detailing which fields failed validation.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("Validation error for request [{}]: {}", request.getDescription(false), errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * A final, catch-all handler for any other unhandled exceptions.
     * This prevents stack traces from being exposed to the client and provides a generic
     * "500 Internal Server Error" response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetailsDto> handleGlobalException(Exception exception, WebRequest webRequest) {
        // Log the full stack trace for debugging purposes
        log.error("An unhandled exception occurred: ", exception);

        ErrorDetailsDto errorDetails = new ErrorDetailsDto(
                LocalDateTime.now(),
                "An internal server error occurred. Please try again later.",
                "uri=" + webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}