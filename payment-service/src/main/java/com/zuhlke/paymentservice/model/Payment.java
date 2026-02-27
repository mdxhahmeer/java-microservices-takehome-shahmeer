package com.zuhlke.paymentservice.model;

import java.time.Instant;
import java.util.UUID;

public record Payment(UUID id, UUID orderId, double amount, Instant processedAt) {}
