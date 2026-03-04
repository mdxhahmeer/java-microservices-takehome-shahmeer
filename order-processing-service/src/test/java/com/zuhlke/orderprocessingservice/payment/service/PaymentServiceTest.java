package com.zuhlke.orderprocessingservice.payment.service;

import com.zuhlke.common.event.OrderCreatedEvent;
import com.zuhlke.orderprocessingservice.payment.dto.PaymentResponse;
import com.zuhlke.orderprocessingservice.payment.repository.PaymentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentServiceTest {

  private PaymentService paymentService;

  private static final String TEST_EMAIL_1 = "test@gmail.com";
  private static final String TEST_EMAIL_2 = "example@gmail.com";

  private static final BigDecimal TEST_AMOUNT_1 = BigDecimal.valueOf(49.90);
  private static final BigDecimal TEST_AMOUNT_2 = BigDecimal.valueOf(9.90);

  @BeforeEach
  void setUp() {
    PaymentRepo paymentRepo = new PaymentRepo();
    ApplicationEventPublisher eventPublisher = event -> {};
    paymentService = new PaymentService(paymentRepo, eventPublisher);
  }

  @Test
  void handleOrderCreated_shouldProcessPaymentWithCorrectDetails() {
    UUID orderId = UUID.randomUUID();
    OrderCreatedEvent event = new OrderCreatedEvent(orderId, TEST_AMOUNT_1, TEST_EMAIL_1);

    paymentService.handleOrderCreated(event);

    List<PaymentResponse> payments = paymentService.getAllPayments();
    assertThat(payments).hasSize(1);
    assertThat(payments.getFirst().orderId()).isEqualTo(orderId);
    assertThat(payments.getFirst().amount()).isEqualTo(TEST_AMOUNT_1);
    assertThat(payments.getFirst().id()).isNotNull();
    assertThat(payments.getFirst().processedAt()).isNotNull();
  }

  @Test
  void handleOrderCreated_shouldNotProcessDuplicatePayment() {
    UUID orderId = UUID.randomUUID();
    OrderCreatedEvent event = new OrderCreatedEvent(orderId, TEST_AMOUNT_1, TEST_EMAIL_1);

    paymentService.handleOrderCreated(event);
    paymentService.handleOrderCreated(event);

    assertThat(paymentService.getAllPayments()).hasSize(1);
  }

  @Test
  void getAllPayments_shouldReturnAllPayments() {
    paymentService.handleOrderCreated(new OrderCreatedEvent(UUID.randomUUID(), TEST_AMOUNT_1, TEST_EMAIL_1));
    paymentService.handleOrderCreated(new OrderCreatedEvent(UUID.randomUUID(), TEST_AMOUNT_2, TEST_EMAIL_2));

    List<PaymentResponse> payments = paymentService.getAllPayments();

    assertThat(payments).hasSize(2);
  }
}
