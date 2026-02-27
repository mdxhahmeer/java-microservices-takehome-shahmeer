package com.zuhlke.paymentservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler
  public ResponseEntity<Map<String, Object>> handlePaymentNotFound(
      PaymentNotFoundException exception) {
    LOGGER.warn("Payment not found: {}", exception.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(exception.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception exception) {
    LOGGER.error("Unexpected error: {}", exception.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorBody(exception.getMessage()));
  }

  private Map<String, Object> errorBody(String message) {
    return Map.of("message", message, "timestamp", Instant.now().toString());
  }
}
