package com.zuhlke.orderservice.controller;

import com.zuhlke.orderservice.dto.CreateOrderRequest;
import com.zuhlke.orderservice.dto.OrderResponse;
import com.zuhlke.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest orderRequest) {
    OrderResponse orderResponse = orderService.createOrder(orderRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID id) {
    return ResponseEntity.ok(orderService.getOrderById(id));
  }

  @GetMapping
  public ResponseEntity<List<OrderResponse>> getAllOrders() {
    return ResponseEntity.ok(orderService.getAllOrders());
  }
}
