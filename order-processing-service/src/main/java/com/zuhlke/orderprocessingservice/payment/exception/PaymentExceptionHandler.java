package com.zuhlke.orderprocessingservice.payment.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class PaymentExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentExceptionHandler.class);

  @ExceptionHandler
  public ResponseEntity<Map<String, Object>> handlePaymentNotFound(
      PaymentNotFoundException exception) {
    LOGGER.warn("Payment not found: {}", exception.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("message", exception.getMessage(), "timestamp", Instant.now().toString()));
  }
}
