package com.zuhlke.orderservice.model;

import java.time.Instant;
import java.util.UUID;

public record Order(
        UUID id,
        String customerEmail,
        double amount,
        Instant createdAt
) {}
