package com.zuhlke.orderprocessingservice.notification.service;

import com.zuhlke.common.event.PaymentSucceededEvent;
import com.zuhlke.orderprocessingservice.notification.domain.Notification;
import com.zuhlke.orderprocessingservice.notification.dto.NotificationResponse;
import com.zuhlke.orderprocessingservice.notification.repository.NotificationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
  @Mock NotificationRepo notificationRepo;

  @InjectMocks NotificationService notificationService;

  private static final Instant NOW = Instant.now();

  @Test
  void handlePaymentSucceeded_shouldSaveNotification() {
    PaymentSucceededEvent event =
        new PaymentSucceededEvent(this, UUID.randomUUID(), UUID.randomUUID(), 49.99, NOW);
    Notification savedNotification =
        new Notification(
            UUID.randomUUID(), event.getOrderId(), event.getPaymentId(), event.getAmount(), NOW);
    when(notificationRepo.save(any(Notification.class))).thenReturn(savedNotification);

    notificationService.handlePaymentSucceeded(event);
    verify(notificationRepo, times(1)).save(any(Notification.class));
  }

  @Test
  void handlePaymentSucceeded_shouldSaveNotificationWithCorrectDetails() {
    UUID orderId = UUID.randomUUID();
    UUID paymentId = UUID.randomUUID();
    PaymentSucceededEvent event =
        new PaymentSucceededEvent(this, orderId, paymentId, 49.99, Instant.now());
    when(notificationRepo.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

    notificationService.handlePaymentSucceeded(event);

    ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepo).save(notificationCaptor.capture());
    Notification savedNotification = notificationCaptor.getValue();
    assertThat(savedNotification.orderId()).isEqualTo(orderId);
    assertThat(savedNotification.paymentId()).isEqualTo(paymentId);
    assertThat(savedNotification.amount()).isEqualTo(49.99);
  }

  @Test
  void getAllNotifications_shouldReturnAllNotifications() {
    List<Notification> notifications =
        List.of(
            new Notification(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 49.99, Instant.now()),
            new Notification(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 99.99, Instant.now()));
    when(notificationRepo.findAll()).thenReturn(notifications);

    List<NotificationResponse> response = notificationService.getAllNotifications();

    assertThat(response).hasSize(2);
    verify(notificationRepo, times(1)).findAll();
  }
}
