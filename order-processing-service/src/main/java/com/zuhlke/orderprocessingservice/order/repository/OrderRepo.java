package com.zuhlke.orderprocessingservice.order.repository;

import com.zuhlke.orderprocessingservice.order.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrderRepo {
  private final ConcurrentHashMap<UUID, Order> orders = new ConcurrentHashMap<>();

  public Order save(Order order) {
    orders.put(order.id(), order);
    return order;
  }

  public List<Order> findAll() {
    return List.copyOf(orders.values());
  }

  public Order findById(UUID id) {
    return orders.get(id);
  }
}
