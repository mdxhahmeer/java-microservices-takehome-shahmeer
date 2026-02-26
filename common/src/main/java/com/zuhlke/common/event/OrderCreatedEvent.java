package com.zuhlke.common.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class OrderCreatedEvent extends ApplicationEvent {

  private final UUID orderId;
  private final double amount;
  private final String customerEmail;

  public OrderCreatedEvent(Object source, UUID orderId, double amount, String customerEmail) {
    super(source);
    this.orderId = orderId;
    this.amount = amount;
    this.customerEmail = customerEmail;
  }

  public UUID getOrderId() {
    return orderId;
  }

  public double getAmount() {
    return amount;
  }

  public String getCustomerEmail() {
    return customerEmail;
  }
}
