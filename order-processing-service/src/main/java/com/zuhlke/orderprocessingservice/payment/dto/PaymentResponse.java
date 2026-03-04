package com.zuhlke.orderprocessingservice.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(UUID id, UUID orderId, BigDecimal amount, Instant processedAt) {}
