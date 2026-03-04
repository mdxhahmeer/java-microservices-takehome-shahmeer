package com.zuhlke.orderprocessingservice.payment.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Payment(UUID id, UUID orderId, BigDecimal amount, Instant processedAt) {}
