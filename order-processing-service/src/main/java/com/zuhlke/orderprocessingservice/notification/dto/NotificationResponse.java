package com.zuhlke.orderprocessingservice.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
    UUID id, UUID orderId, UUID paymentId, double amount, Instant sentAt) {}
