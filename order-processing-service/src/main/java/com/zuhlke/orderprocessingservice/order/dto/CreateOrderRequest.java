package com.zuhlke.orderprocessingservice.order.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderRequest(
    @NotBlank(message = "Customer email must not be blank.")
        @Email(message = "Customer email must be a valid email.")
        String customerEmail,
    @NotNull(message = "amount must be provided.") @Positive(message = "amount cannot be negative.")
        BigDecimal amount) {}
