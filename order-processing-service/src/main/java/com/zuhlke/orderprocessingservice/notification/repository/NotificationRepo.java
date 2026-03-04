package com.zuhlke.orderprocessingservice.notification.repository;

import com.zuhlke.orderprocessingservice.notification.domain.Notification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NotificationRepo {
  private final ConcurrentHashMap<UUID, Notification> notificationsByPaymentId = new ConcurrentHashMap<>();

  public Notification save(Notification notification) {
    notificationsByPaymentId.put(notification.paymentId(), notification);
    return notification;
  }

  public boolean existsByPaymentId(UUID paymentId) {
    return notificationsByPaymentId.containsKey(paymentId);
  }

  public List<Notification> findAll() {
    return List.copyOf(notificationsByPaymentId.values());
  }

  public void deleteAll() {
    notificationsByPaymentId.clear();
  }
}
