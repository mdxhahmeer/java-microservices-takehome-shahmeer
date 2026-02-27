package com.zuhlke.orderprocessingservice.paymentservice.dto;

import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(UUID id, UUID orderId, double amount, Instant processedAt) {}
