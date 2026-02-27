package com.zuhlke.orderprocessingservice.notification.controller;

import com.zuhlke.orderprocessingservice.notification.dto.NotificationResponse;
import com.zuhlke.orderprocessingservice.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping
  public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
    return ResponseEntity.ok(notificationService.getAllNotifications());
  }
}
