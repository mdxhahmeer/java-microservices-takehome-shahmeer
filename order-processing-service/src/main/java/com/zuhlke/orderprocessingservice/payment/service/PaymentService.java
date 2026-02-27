package com.zuhlke.orderprocessingservice.payment.service;

import com.zuhlke.common.event.OrderCreatedEvent;
import com.zuhlke.common.event.PaymentSucceededEvent;
import com.zuhlke.orderprocessingservice.payment.dto.PaymentResponse;
import com.zuhlke.orderprocessingservice.payment.domain.Payment;
import com.zuhlke.orderprocessingservice.payment.repository.PaymentRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
  private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

  private final PaymentRepo paymentRepo;
  private final ApplicationEventPublisher eventPublisher;

  public PaymentService(PaymentRepo paymentRepo, ApplicationEventPublisher eventPublisher) {
    this.paymentRepo = paymentRepo;
    this.eventPublisher = eventPublisher;
  }

  @EventListener
  public void handleOrderCreated(OrderCreatedEvent orderCreatedEvent) {
    LOGGER.info("Received OrderCreatedEvent for order id: {}", orderCreatedEvent.getOrderId());

    Payment payment =
        new Payment(
            UUID.randomUUID(),
            orderCreatedEvent.getOrderId(),
            orderCreatedEvent.getAmount(),
            Instant.now());

    paymentRepo.save(payment);
    LOGGER.info("Payment processed with id: {}", payment.id());

    eventPublisher.publishEvent(
        new PaymentSucceededEvent(
            this, payment.orderId(), payment.id(), payment.amount(), payment.processedAt()));
    LOGGER.debug("PaymentSucceededEvent published for payment id: {}", payment.id());
  }

  public List<PaymentResponse> getAllPayments() {
    return paymentRepo.findAll().stream().map(this::toResponse).toList();
  }

  private PaymentResponse toResponse(Payment payment) {
    return new PaymentResponse(
        payment.id(), payment.orderId(), payment.amount(), payment.processedAt());
  }
}
