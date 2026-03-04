package com.zuhlke.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentSucceededEvent(
    UUID orderId, UUID paymentId, BigDecimal amount, Instant timestamp) {}
