package com.zuhlke.orderprocessingservice.order.controller;

import com.zuhlke.orderprocessingservice.order.dto.CreateOrderRequest;
import com.zuhlke.orderprocessingservice.order.dto.OrderResponse;
import com.zuhlke.orderprocessingservice.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
  @Autowired private MockMvc mockMvc;

  @Autowired ObjectMapper objectMapper;

  @MockitoBean private OrderService orderService;

  private static final Instant NOW = Instant.now();

  private static final String TEST_EMAIL_1 = "test@gmail.com";
  private static final String TEST_EMAIL_2 = "example@gmail.com";

  private static final BigDecimal TEST_AMOUNT_1 = BigDecimal.valueOf(49.90);
  private static final BigDecimal TEST_AMOUNT_2 = BigDecimal.valueOf(99.90);

  @Test
  void createOrder_shouldReturn201_whenRequestIsValid() throws Exception {
    CreateOrderRequest orderRequest = new CreateOrderRequest(TEST_EMAIL_1, TEST_AMOUNT_1);
    OrderResponse response = new OrderResponse(UUID.randomUUID(), TEST_EMAIL_1, TEST_AMOUNT_1, NOW);
    when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(response);

    mockMvc
        .perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.customerEmail").value(TEST_EMAIL_1))
        .andExpect(jsonPath("$.amount").value(TEST_AMOUNT_1));
  }

  @Test
  void createOrder_shouldReturn400_whenEmailIsInvalid() throws Exception {
    CreateOrderRequest request = new CreateOrderRequest("not-an-email", TEST_AMOUNT_1);

    mockMvc
        .perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void createOrder_shouldReturn400_whenAmountIsNegative() throws Exception {
    CreateOrderRequest request = new CreateOrderRequest(TEST_EMAIL_1, BigDecimal.valueOf(-10.0));

    mockMvc
        .perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void createOrder_shouldReturn400_whenEmailIsBlank() throws Exception {
    CreateOrderRequest request = new CreateOrderRequest("", TEST_AMOUNT_1);

    mockMvc
        .perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  void getAllOrders_shouldReturn200_withListOfOrders() throws Exception {
    List<OrderResponse> orders =
        List.of(
            new OrderResponse(UUID.randomUUID(), TEST_EMAIL_1, TEST_AMOUNT_1, Instant.now()),
            new OrderResponse(UUID.randomUUID(), TEST_EMAIL_2, TEST_AMOUNT_2, Instant.now()));
    when(orderService.getAllOrders()).thenReturn(orders);

    mockMvc
        .perform(get("/api/orders"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void getOrderById_shouldReturn200_whenOrderExists() throws Exception {
    UUID id = UUID.randomUUID();
    OrderResponse response = new OrderResponse(id, TEST_EMAIL_1, TEST_AMOUNT_1, Instant.now());
    when(orderService.getOrderById(id)).thenReturn(response);

    mockMvc
        .perform(get("/api/orders/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()));
  }
}
