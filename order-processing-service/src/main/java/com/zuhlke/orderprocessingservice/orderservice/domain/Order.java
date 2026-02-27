package com.zuhlke.orderprocessingservice.orderservice.domain;

import java.time.Instant;
import java.util.UUID;

public record Order(
        UUID id,
        String customerEmail,
        double amount,
        Instant createdAt
) {}
