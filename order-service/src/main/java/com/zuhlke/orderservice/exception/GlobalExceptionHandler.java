package com.zuhlke.orderservice.exception;

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
  private final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleOrderNotFound(OrderNotFoundException exception) {
    LOGGER.warn("Order not found: {}", exception.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(exception.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception exception) {
      LOGGER.error("Whoops, unexpected error!!: {}", exception.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody("An unexpected error occurred"));
  }

  private Map<String, Object> errorBody(String message) {
    return Map.of("message", message, "timestamp", Instant.now().toString());
  }
}
