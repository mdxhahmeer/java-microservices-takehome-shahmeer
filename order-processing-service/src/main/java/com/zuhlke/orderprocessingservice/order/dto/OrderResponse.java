package com.zuhlke.orderprocessingservice.order.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderResponse(UUID id, String customerEmail, double amount, Instant createdAt) {}
