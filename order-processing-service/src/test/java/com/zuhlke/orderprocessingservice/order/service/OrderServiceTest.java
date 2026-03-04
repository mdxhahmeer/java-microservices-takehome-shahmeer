package com.zuhlke.orderprocessingservice.order.service;

import com.zuhlke.orderprocessingservice.order.dto.CreateOrderRequest;
import com.zuhlke.orderprocessingservice.order.dto.OrderResponse;
import com.zuhlke.orderprocessingservice.order.exception.OrderNotFoundException;
import com.zuhlke.orderprocessingservice.order.repository.OrderRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderServiceTest {
  private OrderService orderService;

  private static final String TEST_EMAIL_1 = "test@gmail.com";
  private static final String TEST_EMAIL_2 = "example@gmail.com";

  private static final BigDecimal TEST_AMOUNT_1 = BigDecimal.valueOf(49.90);
  private static final BigDecimal TEST_AMOUNT_2 = BigDecimal.valueOf(9.90);

  @BeforeEach
  void setUp() {
    OrderRepo orderRepo = new OrderRepo();
    ApplicationEventPublisher eventPublisher = event -> {};
    orderService = new OrderService(orderRepo, eventPublisher);
  }

  @Test
  void createOrder_shouldReturnOrderWithCorrectDetails() {
    CreateOrderRequest orderRequest = new CreateOrderRequest(TEST_EMAIL_1, TEST_AMOUNT_1);
    OrderResponse orderResponse = orderService.createOrder(orderRequest);

    assertThat(orderResponse.customerEmail()).isEqualTo(TEST_EMAIL_1);
    assertThat(orderResponse.amount()).isEqualTo(TEST_AMOUNT_1);
    assertThat(orderResponse.id()).isNotNull();
    assertThat(orderResponse.createdAt()).isNotNull();
  }

  @Test
  void createOrder_shouldStoreOrderInRepository() {
    CreateOrderRequest request = new CreateOrderRequest(TEST_EMAIL_1, TEST_AMOUNT_1);

    orderService.createOrder(request);

    List<OrderResponse> orders = orderService.getAllOrders();

    assertThat(orders).hasSize(1);
    assertThat(orders.getFirst().customerEmail()).isEqualTo(TEST_EMAIL_1);
  }

  @Test
  void getAllOrders_shouldReturnAllOrders() {
    orderService.createOrder(new CreateOrderRequest(TEST_EMAIL_1, TEST_AMOUNT_1));
    orderService.createOrder(new CreateOrderRequest(TEST_EMAIL_2, TEST_AMOUNT_2));

    List<OrderResponse> orders = orderService.getAllOrders();

    assertThat(orders).hasSize(2);
  }

  @Test
  void getOrderById_shouldReturnOrder_whenOrderExists() {
    CreateOrderRequest request = new CreateOrderRequest(TEST_EMAIL_1, TEST_AMOUNT_1);
    OrderResponse created = orderService.createOrder(request);

    OrderResponse found = orderService.getOrderById(created.id());

    assertThat(found.id()).isEqualTo(created.id());
    assertThat(found.customerEmail()).isEqualTo(TEST_EMAIL_1);
  }

  @Test
  void getOrderById_shouldThrowOrderNotFoundException_whenOrderDoesNotExist() {
    CreateOrderRequest request = new CreateOrderRequest(TEST_EMAIL_1, TEST_AMOUNT_1);
    orderService.createOrder(request);

    assertThatThrownBy(() -> orderService.getOrderById(java.util.UUID.randomUUID()))
        .isInstanceOf(OrderNotFoundException.class);
  }
}
