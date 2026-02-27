package com.zuhlke.orderprocessingservice.order.service;

import com.zuhlke.common.event.OrderCreatedEvent;
import com.zuhlke.orderprocessingservice.order.dto.CreateOrderRequest;
import com.zuhlke.orderprocessingservice.order.dto.OrderResponse;
import com.zuhlke.orderprocessingservice.order.exception.OrderNotFoundException;
import com.zuhlke.orderprocessingservice.order.domain.Order;
import com.zuhlke.orderprocessingservice.order.repository.OrderRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
  private final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

  private final OrderRepo orderRepo;
  private final ApplicationEventPublisher eventPublisher;

  public OrderService(OrderRepo orderRepo, ApplicationEventPublisher eventPublisher) {
    this.orderRepo = orderRepo;
    this.eventPublisher = eventPublisher;
  }

  public OrderResponse createOrder(CreateOrderRequest orderRequest) {
    Order order =
        new Order(
            UUID.randomUUID(), orderRequest.customerEmail(), orderRequest.amount(), Instant.now());

    orderRepo.save(order);
    LOGGER.info("Order created with id: {}", order.id());

    eventPublisher.publishEvent(
        new OrderCreatedEvent(this, order.id(), order.amount(), order.customerEmail()));
    LOGGER.debug("OrderCreatedEvent published for order id: {}", order.id());

    return toResponse(order);
  }

  public OrderResponse getOrderById(UUID id) {
    Order order = orderRepo.findById(id);
    if (order == null) {
      throw new OrderNotFoundException(id);
    }
    return toResponse(order);
  }

  public List<OrderResponse> getAllOrders() {
    return orderRepo.findAll().stream().map(this::toResponse).toList();
  }

  private OrderResponse toResponse(Order order) {
    return new OrderResponse(order.id(), order.customerEmail(), order.amount(), order.createdAt());
  }
}
