package com.zuhlke.orderprocessingservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zuhlke.orderprocessingservice.notification.repository.NotificationRepo;
import com.zuhlke.orderprocessingservice.order.dto.CreateOrderRequest;
import com.zuhlke.orderprocessingservice.order.dto.OrderResponse;
import com.zuhlke.orderprocessingservice.order.repository.OrderRepo;
import com.zuhlke.orderprocessingservice.payment.repository.PaymentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderIntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private OrderRepo orderRepo;

  @Autowired private PaymentRepo paymentRepo;

  @Autowired private NotificationRepo notificationRepo;

  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

  private static final String TEST_EMAIL_1 = "test@gmail.com";
  private static final String TEST_EMAIL_2 = "example@gmail.com";

  private static final BigDecimal TEST_AMOUNT_1 = BigDecimal.valueOf(29.99);
  private static final BigDecimal TEST_AMOUNT_2 = BigDecimal.valueOf(9.99);

  @BeforeEach
  void setUp() {
    orderRepo.deleteAll();
    paymentRepo.deleteAll();
    notificationRepo.deleteAll();
  }

  @Test
  void createOrder_shouldTriggerFullEventFlow() throws Exception {
    CreateOrderRequest orderRequest = new CreateOrderRequest(TEST_EMAIL_1, TEST_AMOUNT_1);

    MvcResult result =
        mockMvc
            .perform(
                post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderRequest)))
            .andExpect(status().isCreated())
            .andReturn();

    OrderResponse orderResponse =
        objectMapper.readValue(result.getResponse().getContentAsString(), OrderResponse.class);

    // wait for async event processing
    Thread.sleep(500);

    assertThat(orderResponse).isNotNull();
    assertThat(orderResponse.customerEmail()).isEqualTo(TEST_EMAIL_1);
    assertThat(orderResponse.amount()).isEqualTo(TEST_AMOUNT_1);

    // verify payment was processed
    assertThat(paymentRepo.findAll()).hasSize(1);
    assertThat(paymentRepo.existsByOrderId(orderResponse.id())).isTrue();

    // verify notification was sent
    assertThat(notificationRepo.findAll()).hasSize(1);
    assertThat(notificationRepo.existsByPaymentId(paymentRepo.findAll().getFirst().id())).isTrue();
  }

  @Test
  void createOrder_shouldReturn400_whenEmailIsInvalid() throws Exception {
    CreateOrderRequest orderRequest = new CreateOrderRequest("randomString", TEST_AMOUNT_1);

    mockMvc
        .perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists());

    assertThat(paymentRepo.findAll()).isEmpty();
    assertThat(notificationRepo.findAll()).isEmpty();
  }

  @Test
  void createMultipleOrders_shouldCreateMultiplePaymentsAndNotifications() throws Exception {
    CreateOrderRequest orderRequest1 = new CreateOrderRequest(TEST_EMAIL_1, TEST_AMOUNT_1);
    CreateOrderRequest orderRequest2 = new CreateOrderRequest(TEST_EMAIL_2, TEST_AMOUNT_2);

    mockMvc
        .perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest1)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest2)))
        .andExpect(status().isCreated());

    // wait for async event processing
    Thread.sleep(500);

    assertThat(orderRepo.findAll()).hasSize(2);
    assertThat(paymentRepo.findAll()).hasSize(2);
    assertThat(notificationRepo.findAll()).hasSize(2);
  }
}
