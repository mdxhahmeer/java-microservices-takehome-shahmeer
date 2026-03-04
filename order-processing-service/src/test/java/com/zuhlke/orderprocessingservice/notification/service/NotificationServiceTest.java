package com.zuhlke.orderprocessingservice.notification.service;

import com.zuhlke.common.event.PaymentSucceededEvent;
import com.zuhlke.orderprocessingservice.notification.dto.NotificationResponse;
import com.zuhlke.orderprocessingservice.notification.repository.NotificationRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationServiceTest {

  private NotificationService notificationService;

  private static final BigDecimal TEST_AMOUNT_1 = BigDecimal.valueOf(49.90);
  private static final BigDecimal TEST_AMOUNT_2 = BigDecimal.valueOf(9.90);

  @BeforeEach
  void setUp() {
    NotificationRepo notificationRepo = new NotificationRepo();
    notificationService = new NotificationService(notificationRepo);
  }

  @Test
  void handlePaymentSucceeded_shouldSaveNotificationWithCorrectDetails() {
    UUID orderId = UUID.randomUUID();
    UUID paymentId = UUID.randomUUID();
    PaymentSucceededEvent event = new PaymentSucceededEvent(orderId, paymentId, TEST_AMOUNT_1, Instant.now());

    notificationService.handlePaymentSucceeded(event);

    List<NotificationResponse> notifications = notificationService.getAllNotifications();
    assertThat(notifications).hasSize(1);
    assertThat(notifications.getFirst().orderId()).isEqualTo(orderId);
    assertThat(notifications.getFirst().paymentId()).isEqualTo(paymentId);
    assertThat(notifications.getFirst().amount()).isEqualTo(TEST_AMOUNT_1);
    assertThat(notifications.getFirst().sentAt()).isNotNull();
  }

  @Test
  void getAllNotifications_shouldReturnAllNotifications() {
    notificationService.handlePaymentSucceeded(
            new PaymentSucceededEvent(UUID.randomUUID(), UUID.randomUUID(), TEST_AMOUNT_1, Instant.now()));
    notificationService.handlePaymentSucceeded(
            new PaymentSucceededEvent(UUID.randomUUID(), UUID.randomUUID(), TEST_AMOUNT_2, Instant.now()));

    List<NotificationResponse> notifications = notificationService.getAllNotifications();

    assertThat(notifications).hasSize(2);
  }
}