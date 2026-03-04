package com.zuhlke.orderprocessingservice.payment.repository;

import com.zuhlke.orderprocessingservice.payment.domain.Payment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PaymentRepo {
  private final ConcurrentHashMap<UUID, Payment> payments = new ConcurrentHashMap<>();

  public Payment save(Payment payment) {
    payments.put(payment.id(), payment);
    return payment;
  }

  public List<Payment> findAll() {
    return List.copyOf(payments.values());
  }

  public Payment findById(UUID id) {
    return payments.get(id);
  }

  public boolean existsByOrderId(UUID orderId) {
    return payments.values().stream()
            .anyMatch(payment -> payment.orderId().equals(orderId));
  }

  public void deleteAll() {
    payments.clear();
  }
}
