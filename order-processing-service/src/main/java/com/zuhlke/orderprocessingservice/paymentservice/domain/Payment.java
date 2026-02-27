package com.zuhlke.orderprocessingservice.paymentservice.domain;

import java.time.Instant;
import java.util.UUID;

public record Payment(UUID id, UUID orderId, double amount, Instant processedAt) {}
