package com.zuhlke.orderprocessingservice.notification.repository;

import com.zuhlke.orderprocessingservice.notification.domain.Notification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NotificationRepo {
  private final ConcurrentHashMap<UUID, Notification> notifications = new ConcurrentHashMap<>();

  public Notification save(Notification notification) {
    notifications.put(notification.id(), notification);
    return notification;
  }

  public List<Notification> findAll() {
    return List.copyOf(notifications.values());
  }
}
