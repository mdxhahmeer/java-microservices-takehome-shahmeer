# Order Processing System — Take-Home Test

A simplified microservices-based order processing system built with Java 21 and Spring Boot 4.0.3.

---

## Architecture Overview

The system is composed of three logical services running within a single Spring Boot application,
communicating via Spring's `ApplicationEventPublisher`:

- **Order Service** — handles order creation and publishes `OrderCreatedEvent`
- **Payment Service** — listens for `OrderCreatedEvent`, processes payment asynchronously, and publishes `PaymentSucceededEvent`
- **Notification Service** — listens for `PaymentSucceededEvent` and logs a notification

### Event Flow

```
POST /api/orders
      │
      ▼
OrderService creates order
      │ publishes OrderCreatedEvent
      ▼
PaymentService processes payment asynchronously (@Async)
      │ publishes PaymentSucceededEvent
      ▼
NotificationService logs notification
```

### Project Structure

```
order-processing-application/
├── common/                          # Shared event classes
│   └── src/main/java/com/zuhlke/common/
│       └── event/
│           ├── OrderCreatedEvent.java
│           └── PaymentSucceededEvent.java
├── order-processing-service/        # Single Spring Boot application
│   └── src/main/java/com/zuhlke/orderprocessingservice/
│       ├── order/                   # Order Service
│       │   ├── controller/
│       │   ├── service/
│       │   ├── domain/
│       │   ├── repository/
│       │   ├── dto/
│       │   └── exception/
│       ├── payment/                 # Payment Service
│       │   ├── controller/
│       │   ├── service/
│       │   ├── domain/
│       │   ├── repository/
│       │   ├── dto/
│       │   └── exception/
│       └── notification/            # Notification Service
│           ├── controller/
│           ├── service/
│           ├── domain/
│           ├── repository/
│           ├── dto/
│           └── exception/
└── README.md
```

---

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.3**
- **Spring Web** — REST APIs
- **Spring Context** — In-memory event bus via `ApplicationEventPublisher`
- **Spring Async** — Asynchronous event processing via `@Async` and `@EnableAsync`
- **SpringDoc OpenAPI 3.0.1** — Swagger UI
- **Jakarta Validation** — Bean validation on request DTOs
- **JUnit 5 + Mockito** — Unit tests
- **MockMvc + SpringBootTest** — Integration tests
- **Maven** — Dependency management

---

## How to Run Locally

### Prerequisites
- Java 21
- Maven 3.x

### Steps

1. Clone the repository:
```bash
git clone https://github.com/mdxhahmeer/java-microservices-takehome-shahmeer.git
cd java-microservices-takehome-shahmeer/order-processing-application
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
cd order-processing-service
mvn spring-boot:run
```

4. Access Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

---

## API Endpoints

### Order Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create a new order |
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{id}` | Get order by ID |

### Payment Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/payments` | Get all payments |

### Notification Service
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/notifications` | Get all notifications |

---

## Testing the Event Flow

1. Create an order via `POST /api/orders`:
```json
{
    "customerEmail": "test@example.com",
    "amount": 49.99
}
```

2. Verify payment was automatically processed via `GET /api/payments`
3. Verify notification was sent via `GET /api/notifications`

---

## Design Decisions & Assumptions

### Single JVM Architecture
All three services run within a single Spring Boot application using Spring's `ApplicationEventPublisher`
for in-memory event communication. This was chosen to:
- Demonstrate clean event-driven architecture without infrastructure complexity
- Focus on code quality, layering, and separation of concerns
- Complete the assignment within the stipulated time

In a production system, each service would be a separate deployable unit communicating via
RabbitMQ or Kafka with minimal changes to business logic.

### Asynchronous Event Processing
Payment processing is handled asynchronously using Spring's `@Async` annotation combined with
`@EventListener`. This better reflects real-world event-driven behaviour where payment processing
happens independently of the HTTP request thread. `@EnableAsync` is configured on the main
application class.

### Idempotency Check
The Payment Service includes an idempotency check using `existsByOrderId()` before processing
a payment. This prevents duplicate payments if the same `OrderCreatedEvent` is published more
than once, which is a common defensive pattern in event-driven systems.

### In-Memory Storage
Data is stored in `ConcurrentHashMap` collections which are thread-safe and suitable for
concurrent async event processing:
- `OrderRepository` — keyed by `UUID id`
- `PaymentRepository` — keyed by `UUID id` with `existsByOrderId()` for idempotency checks
- `NotificationRepo` — keyed by `UUID paymentId` for efficient `existsByPaymentId()` lookups

Data does not persist between application restarts. In a production system, this would be
replaced by a relational database (e.g. PostgreSQL) with Spring Data JPA.

### Bean Validation
Request DTOs are validated using Jakarta Validation annotations:
- `@NotBlank` and `@Email` on `customerEmail`
- `@Positive` on `amount`

Validation errors return a `400 BAD REQUEST` response with a descriptive message.

### Payment Processing
Payment always succeeds. In a production system, this would integrate with a real payment
gateway with retry logic and failure handling.

### Notifications
Notifications are simulated via console logging. In a production system, this would integrate
with an email or SMS service.

### Event Design
Events are implemented as plain Java records rather than extending `ApplicationEvent`.
This was a deliberate decision to:
- **Eliminate producer/consumer coupling** — no `source` reference means consumers
  have no access to the producer's internal state
- **Enforce immutability** — records have no setters, events cannot be mutated after creation
- **Improve portability** — plain POJOs are easily serializable to JSON, making them
  drop-in replaceable when migrating to a durable event system like RabbitMQ or Kafka
- **Simplify the API** — no need to pass `this` or any source object when publishing events

### No Authentication
No authentication or authorization is implemented. In a production system, Spring Security
with JWT would be used.

---

## Testing

### Unit Tests
Unit tests cover the service layer for all three services using JUnit 5 and Mockito:
- `OrderServiceTest` — tests order creation, event publishing, and retrieval
- `PaymentServiceTest` — tests payment processing and event publishing
- `NotificationServiceTest` — tests notification creation and retrieval

### Controller Tests
- `OrderControllerTest` — tests REST endpoints and bean validation using `@WebMvcTest`

### Integration Tests
- `OrderIntegrationTest` — tests the full event flow end-to-end using `@SpringBootTest`
  and `MockMvc`, verifying that creating an order triggers payment processing and
  notification sending via async events

### Running Tests
```bash
mvn test
```

---

## Known Limitations & Future Improvements

- Replace in-memory storage with PostgreSQL and Spring Data JPA
- Replace `ApplicationEventPublisher` with RabbitMQ or Kafka for true microservices communication
- Add Docker and `docker-compose.yml` for containerization
- Add Spring Security for authentication and authorization
- Add retry logic for failed event delivery using Spring Retry
- Add API Gateway using Spring Cloud Gateway
- Add health checks using Spring Boot Actuator
- Add more comprehensive integration tests covering failure scenarios
