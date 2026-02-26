package com.zuhlke.orderservice.dto;

import java.time.Instant;
import java.util.UUID;

public record OrderResponse(UUID id, String customerEmail, double amount, Instant createdAt) {}
