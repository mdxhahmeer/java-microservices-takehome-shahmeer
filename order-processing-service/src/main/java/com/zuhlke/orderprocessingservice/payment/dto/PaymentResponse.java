package com.zuhlke.orderprocessingservice.payment.dto;

import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(UUID id, UUID orderId, double amount, Instant processedAt) {}
