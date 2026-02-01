package com.helpdesk.ticketstatusservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTicketNotFound(TicketNotFoundException ex) {
        log.error("TicketNotFoundException caught in Status Service: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error", "Ticket Not Found");
        errorResponse.put("message", ex.getMessage());

        log.info("Returning 404 response from Status Service: {}", errorResponse);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("IllegalArgumentException caught in Status Service: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Invalid Input");
        errorResponse.put("message", ex.getMessage());

        log.info("Returning 400 response from Status Service: {}", errorResponse);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleServiceUnavailable(RuntimeException ex) {
        // Don't handle specific exceptions here - let their specific handlers deal with
        // them
        if (ex instanceof HttpMessageNotReadableException ||
                ex instanceof IllegalArgumentException ||
                ex instanceof ConstraintViolationException) {
            throw ex; // Re-throw to be caught by the specific handler
        }

        log.error("RuntimeException caught in Status Service: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());

        String message = ex.getMessage();

        // Handle validation errors from ticket service
        if (message != null && (message.contains("BAD_REQUEST") || message.contains("400")) &&
                (message.contains("Validation failure") || message.contains("positive number"))) {
            log.info("Converting ticket service validation error to Bad Request");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("error", "Invalid Input");
            errorResponse.put("message", "Ticket ID must be a positive number");
            errorResponse.put("hint", "Please provide a valid ticket ID (greater than 0)");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Handle date conversion errors
        if (message != null && message.contains("Failed to convert") && message.contains("LocalDate")) {
            log.info("Converting date conversion error to Bad Request");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("error", "Invalid Date Format");
            errorResponse.put("message", "Invalid date format. Please use YYYY-MM-DD format (e.g., 2026-02-01)");
            errorResponse.put("hint", "Date must be in ISO format: YYYY-MM-DD");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // Check if this is a JSON parsing error that wasn't caught by
        // HttpMessageNotReadableException
        if (message != null && (message.contains("JSON parse error") ||
                message.contains("Unrecognized token") ||
                message.contains("Leading zeroes not allowed") ||
                message.contains("Unexpected character"))) {

            log.info("Converting JSON parsing RuntimeException to Bad Request");
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("error", "Invalid JSON Format");

            if (message.contains("Leading zeroes not allowed")) {
                errorResponse.put("message",
                        "Invalid number format: Leading zeroes are not allowed in JSON. Use '1' instead of '01', '123' instead of '00123'");
                errorResponse.put("hint", "Ticket ID must be a positive number without leading zeros");
            } else if (message.contains("Unrecognized token")) {
                errorResponse.put("message",
                        "Invalid JSON syntax: Unrecognized token. Please check for typos, missing quotes, or invalid values");
                errorResponse.put("hint", "Make sure all strings are in quotes and values are valid JSON");
            } else {
                errorResponse.put("message", "Invalid JSON format. Please check your request body syntax");
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        errorResponse.put("error", "Service Unavailable");
        errorResponse.put("message", ex.getMessage());

        log.info("Returning 503 response from Status Service: {}", errorResponse);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.error("Validation error in Status Service: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Invalid Input");

        // Collect all validation error messages
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        errorResponse.put("message", "Validation failed: " + validationErrors);
        errorResponse.put("details", ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage())));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("Constraint violation in Status Service: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Invalid Input");

        String constraintErrors = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        // Check if it's a positive number validation error
        if (constraintErrors.contains("positive number")) {
            errorResponse.put("message", "Ticket ID must be a positive number");
            errorResponse.put("hint", "Please provide a valid ticket ID (greater than 0)");
        } else {
            errorResponse.put("message", "Validation failed: " + constraintErrors);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        log.error("JSON parsing error in Status Service: {}", ex.getMessage());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Invalid JSON Format");

        String message = ex.getMessage();
        if (message.contains("Leading zeroes not allowed")) {
            errorResponse.put("message",
                    "Invalid number format: Leading zeroes are not allowed in JSON. Use '1' instead of '01', '123' instead of '00123'");
            errorResponse.put("hint", "Ticket ID must be a positive number without leading zeros");
        } else if (message.contains("TicketStatus") || message.contains("not one of the values accepted for Enum")) {
            errorResponse.put("message",
                    "Invalid status value. Valid status values are: OPEN, IN_PROGRESS, RESOLVED, CLOSED");
            errorResponse.put("hint", "Status must be exactly one of these values (case-sensitive)");
        } else if (message.contains("not a valid")) {
            errorResponse.put("message",
                    "Invalid value in JSON. Please check your input values match the expected format");
        } else if (message.contains("Unexpected character")) {
            errorResponse.put("message", "Invalid JSON syntax. Please check for missing quotes, commas, or brackets");
        } else {
            errorResponse.put("message", "Invalid JSON format. Please check your request body syntax");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}