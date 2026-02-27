package com.zuhlke.orderprocessingservice.order.service;

import com.zuhlke.common.event.OrderCreatedEvent;
import com.zuhlke.orderprocessingservice.order.domain.Order;
import com.zuhlke.orderprocessingservice.order.dto.CreateOrderRequest;
import com.zuhlke.orderprocessingservice.order.dto.OrderResponse;
import com.zuhlke.orderprocessingservice.order.exception.OrderNotFoundException;
import com.zuhlke.orderprocessingservice.order.repository.OrderRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
  @Mock private OrderRepo orderRepo;

  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private OrderService orderService;

  private CreateOrderRequest orderRequest;
  private final Instant NOW = Instant.now();

  @BeforeEach
  void setUp() {
    orderRequest = new CreateOrderRequest("fakeemail@scammer.com", 49.99);
  }

  @Test
  void createOrder_shouldSaveOrderAndPublishEvent() {
    Order savedOrder =
        new Order(UUID.randomUUID(), orderRequest.customerEmail(), orderRequest.amount(), NOW);
    when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);

    OrderResponse orderResponse = orderService.createOrder(orderRequest);

    assertThat(orderResponse.customerEmail()).isEqualTo(orderRequest.customerEmail());
    assertThat(orderResponse.amount()).isEqualTo(orderRequest.amount());
    verify(orderRepo, times(1)).save(any(Order.class));
    verify(eventPublisher, times(1)).publishEvent(any(OrderCreatedEvent.class));
  }

  @Test
  void createOrder_shouldPublishEventWithCorrectDetails() {
    Order savedOrder =
        new Order(UUID.randomUUID(), orderRequest.customerEmail(), orderRequest.amount(), NOW);
    when(orderRepo.save(any(Order.class))).thenReturn(savedOrder);

    OrderResponse orderResponse = orderService.createOrder(orderRequest);

    ArgumentCaptor<OrderCreatedEvent> eventArgumentCaptor =
        ArgumentCaptor.forClass(OrderCreatedEvent.class);
    verify(eventPublisher).publishEvent(eventArgumentCaptor.capture());

    OrderCreatedEvent event = eventArgumentCaptor.getValue();
    assertThat(event.getCustomerEmail()).isEqualTo(orderRequest.customerEmail());
    assertThat(event.getAmount()).isEqualTo(orderResponse.amount());
  }

  @Test
  void getAllOrders_shouldReturnAllOrders() {
    List<Order> orders =
        List.of(
            new Order(UUID.randomUUID(), "email1@gmail.com", 67.67, NOW),
            new Order(UUID.randomUUID(), "email2@outlook.com", 20.20, NOW));
    when(orderRepo.findAll()).thenReturn(orders);

    List<OrderResponse> orderResponses = orderService.getAllOrders();

    assertThat(orderResponses).hasSize(2);
    verify(orderRepo, times(1)).findAll();
  }

  @Test
  void getOrderById_shouldReturnOrder_whenOrderExists() {
    UUID id = UUID.randomUUID();
    Order order = new Order(id, "test@gmail.com", 49.90, NOW);
    when(orderRepo.findById(id)).thenReturn(order);

    OrderResponse orderResponse = orderService.getOrderById(id);

    assertThat(orderResponse.id()).isEqualTo(id);
    assertThat(orderResponse.customerEmail()).isEqualTo("test@gmail.com");
    assertThat(orderResponse.amount()).isEqualTo(49.90);
  }

  @Test
  void getOrderById_shouldThrowOrderNotFoundException_whenOrderDoesNotExist() {
    UUID id = UUID.randomUUID();
    when(orderRepo.findById(id)).thenReturn(null);

    assertThatThrownBy(() -> orderService.getOrderById(id))
        .isInstanceOf(OrderNotFoundException.class)
        .hasMessageContaining(id.toString());
  }
}
