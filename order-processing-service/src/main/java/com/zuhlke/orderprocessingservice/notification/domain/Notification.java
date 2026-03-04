package com.zuhlke.orderprocessingservice.notification.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Notification(UUID id, UUID orderId, UUID paymentId, BigDecimal amount, Instant sentAt) {}
