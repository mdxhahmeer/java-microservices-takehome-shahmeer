package com.zuhlke.orderservice.dto;

public record CreateOrderRequest(String customerEmail, double amount) {}
