package com.zuhlke.orderprocessingservice.orderservice.dto;

public record CreateOrderRequest(String customerEmail, double amount) {}
