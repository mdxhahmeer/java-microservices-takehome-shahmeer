package com.zuhlke.orderprocessingservice.order.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record Order(
        UUID id,
        String customerEmail,
        BigDecimal amount,
        Instant createdAt
) {}
