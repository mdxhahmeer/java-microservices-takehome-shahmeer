package com.zuhlke.orderprocessingservice.order.dto;

public record CreateOrderRequest(String customerEmail, double amount) {}
