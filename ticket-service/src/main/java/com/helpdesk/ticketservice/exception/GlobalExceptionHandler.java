package com.helpdesk.ticketservice.exception;

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
    log.error("Ticket not found exception caught: {}", ex.getMessage());

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now());
    errorResponse.put("status", HttpStatus.NOT_FOUND.value());
    errorResponse.put("error", "Ticket Not Found");
    errorResponse.put("message", ex.getMessage());

    log.info("Returning 404 error response: {}", errorResponse);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
    log.error("Validation error: {}", ex.getMessage());

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
    log.error("Constraint violation: {}", ex.getMessage());

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now());
    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
    errorResponse.put("error", "Invalid Input");

    String constraintErrors = ex.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining("; "));

    errorResponse.put("message", "Validation failed: " + constraintErrors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
    log.error("JSON parsing error in Ticket Service: {}", ex.getMessage());

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now());
    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
    errorResponse.put("error", "Invalid JSON Format");

    String message = ex.getMessage();
    if (message.contains("Leading zeroes not allowed")) {
      errorResponse.put("message",
          "Invalid number format: Leading zeroes are not allowed in JSON. Use '1' instead of '01', '123' instead of '00123'");
      errorResponse.put("hint", "Employee ID must be a positive number without leading zeros");
    } else if (message.contains("not a valid")) {
      errorResponse.put("message", "Invalid value in JSON. Please check your input values match the expected format");
    } else if (message.contains("Unexpected character")) {
      errorResponse.put("message", "Invalid JSON syntax. Please check for missing quotes, commas, or brackets");
    } else {
      errorResponse.put("message", "Invalid JSON format. Please check your request body syntax");
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
    // Don't handle HttpMessageNotReadableException here - let the specific handler
    // deal with it
    if (ex instanceof HttpMessageNotReadableException) {
      throw ex; // Re-throw to be caught by the specific handler
    }

    log.error("RuntimeException caught in Ticket Service: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());

    // Check if this is a JSON parsing error that wasn't caught by
    // HttpMessageNotReadableException
    String message = ex.getMessage();
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
        errorResponse.put("hint", "Employee ID must be a positive number without leading zeros");
      } else if (message.contains("Unrecognized token")) {
        errorResponse.put("message",
            "Invalid JSON syntax: Unrecognized token. Please check for typos, missing quotes, or invalid values");
        errorResponse.put("hint", "Make sure all strings are in quotes and values are valid JSON");
      } else {
        errorResponse.put("message", "Invalid JSON format. Please check your request body syntax");
      }

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // For other RuntimeExceptions, return 500 Internal Server Error
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("timestamp", LocalDateTime.now());
    errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorResponse.put("error", "Internal Server Error");
    errorResponse.put("message", "An unexpected error occurred");

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}