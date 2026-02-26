package com.zuhlke.common.event;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

public class PaymentSucceededEvent extends ApplicationEvent {
  private final UUID orderId;
  private final UUID paymentId;
  private final double amount;
  private final Instant timestamp;

  public PaymentSucceededEvent(
      Object source, UUID orderId, UUID paymentId, double amount, Instant timestamp) {
    super(source);
    this.orderId = orderId;
    this.paymentId = paymentId;
    this.amount = amount;
    this.timestamp = timestamp;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public UUID getPaymentId() {
    return paymentId;
  }

  public double getAmount() {
    return amount;
  }

  public Instant getEventTimestamp() {
    return timestamp;
  }
}
