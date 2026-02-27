package com.zuhlke.orderprocessingservice.notification.service;

import com.zuhlke.common.event.PaymentSucceededEvent;
import com.zuhlke.orderprocessingservice.notification.domain.Notification;
import com.zuhlke.orderprocessingservice.notification.dto.NotificationResponse;
import com.zuhlke.orderprocessingservice.notification.repository.NotificationRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

  private final NotificationRepo notificationRepo;

  public NotificationService(NotificationRepo notificationRepo) {
    this.notificationRepo = notificationRepo;
  }

  @EventListener
  void handlePaymentSucceeded(PaymentSucceededEvent paymentSucceededEvent) {
    LOGGER.info(
        "Received PaymentSucceededEvent for order id: {}", paymentSucceededEvent.getOrderId());

    Notification notification =
        new Notification(
            UUID.randomUUID(),
            paymentSucceededEvent.getOrderId(),
            paymentSucceededEvent.getPaymentId(),
            paymentSucceededEvent.getAmount(),
            Instant.now());

    notificationRepo.save(notification);
    LOGGER.info(
        "Notification sent for order id: {} payment id: {}",
        paymentSucceededEvent.getOrderId(),
        paymentSucceededEvent.getPaymentId());
  }

  public List<NotificationResponse> getAllNotifications() {
    return notificationRepo.findAll().stream().map(this::toResponse).toList();
  }

  private NotificationResponse toResponse(Notification notification) {
    return new NotificationResponse(
        notification.id(),
        notification.orderId(),
        notification.paymentId(),
        notification.amount(),
        notification.sentAt());
  }
}
