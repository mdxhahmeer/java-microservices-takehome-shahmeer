package com.zuhlke.orderprocessingservice.payment.service;

import com.zuhlke.common.event.OrderCreatedEvent;
import com.zuhlke.common.event.PaymentSucceededEvent;
import com.zuhlke.orderprocessingservice.payment.domain.Payment;
import com.zuhlke.orderprocessingservice.payment.dto.PaymentResponse;
import com.zuhlke.orderprocessingservice.payment.repository.PaymentRepo;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
  @Mock private PaymentRepo paymentRepo;

  @Mock ApplicationEventPublisher eventPublisher;

  @InjectMocks private PaymentService paymentService;

  private final Instant NOW = Instant.now();

  @Test
  void handleOrderCreated_shouldSavePaymentAndPublishEvent() {
      OrderCreatedEvent event = new OrderCreatedEvent(this, UUID.randomUUID(), 49.49, "test@gmail.com");
      Payment savedPayment = new Payment(UUID.randomUUID(), event.getOrderId(), event.getAmount(), NOW);
      when(paymentRepo.save(any(Payment.class))).thenReturn(savedPayment);

      paymentService.handleOrderCreated(event);

      verify(paymentRepo, times(1)).save(any(Payment.class));
      verify(eventPublisher, times(1)).publishEvent(any(PaymentSucceededEvent.class));
  }

  @Test
  void handleOrderCreated_shouldPublishEventWithCorrectDetails() {
      UUID orderId = UUID.randomUUID();
      OrderCreatedEvent event = new OrderCreatedEvent(this, orderId, 49.99, "test@gmail.com");
      Payment savedPayment = new Payment(UUID.randomUUID(), event.getOrderId(), event.getAmount(), NOW);

      when(paymentRepo.save(any(Payment.class))).thenReturn(savedPayment);

      paymentService.handleOrderCreated(event);
      ArgumentCaptor<PaymentSucceededEvent> eventArgumentCaptor = ArgumentCaptor.forClass(PaymentSucceededEvent.class);
      verify(eventPublisher).publishEvent(eventArgumentCaptor.capture());

      PaymentSucceededEvent publishedEvent = eventArgumentCaptor.getValue();
      assertThat(publishedEvent.getOrderId()).isEqualTo(orderId);
      assertThat(publishedEvent.getAmount()).isEqualTo(49.99);
  }

    @Test
    void getAllOrders_shouldReturnAllOrders() {
        List<Payment> payments =
                List.of(
                        new Payment(UUID.randomUUID(), UUID.randomUUID(), 67.67, NOW),
                        new Payment(UUID.randomUUID(), UUID.randomUUID(), 20.20, NOW));
        when(paymentRepo.findAll()).thenReturn(payments);

        List<PaymentResponse> paymentResponses = paymentService.getAllPayments();

        assertThat(paymentResponses).hasSize(2);
        verify(paymentRepo, times(1)).findAll();
    }
}
