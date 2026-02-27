package com.zuhlke.orderprocessingservice.notification.domain;

import java.time.Instant;
import java.util.UUID;

public record Notification(UUID id, UUID orderId, UUID paymentId, double amount, Instant sentAt) {}
