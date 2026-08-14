# Restaurant Orders Microservice

## Overview

A reactive microservice for restaurant table order management built with **Spring WebFlux**, **Project Reactor**, and **MongoDB** using **Hexagonal Architecture** (Ports & Adapters).

## Tech Stack

- **Java 17+**
- **Spring Boot 3.2** (WebFlux)
- **Project Reactor**
- **MongoDB** (Reactive Driver)
- **Gradle** (Build)
- **Lombok**
- **JUnit 5 + Mockito + Reactor Test**

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    HTTP Adapters (In)                        │
│  OrderEndpoints + OrderRouter (WebFlux Functional)          │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│              Application Layer (Service)                     │
│         OrderApplicationService                              │
└───────┬────────────────────────────────┬────────────────────┘
        │                                │
┌───────▼──────────────┐    ┌───────────▼────────────────────┐
│  Inbound Ports       │    │   Outbound Ports               │
│  (Use Cases)         │    │   (Repository Interface)       │
│  - CreateOrderUseCase│    │   - OrderRepositoryPort        │
│  - GetOrderUseCase   │    │                                │
│  - ListOrdersUseCase │    │   ┌──────────────────────────┐ │
│  - UpdateStatusUseCase│   │   │  MongoDB Adapter         │ │
│  - DeleteOrderUseCase│    │   │  OrderMongoRepository     │ │
│  - AddItemUseCase    │    │   └──────────────────────────┘ │
│  - RemoveItemUseCase │    │                                │
└───────┬──────────────┘    └────────────────────────────────┘
        │
┌───────▼─────────────────────────────────────────────────────┐
│                     Domain Layer                             │
│  ┌──────────┐  ┌─────────────┐  ┌──────────────────────┐   │
│  │  Order   │  │ OrderItem   │  │  OrderStatus (enum)  │   │
│  │ (Entity) │  │ (Value Obj) │  │  State machine rules │   │
│  └──────────┘  └─────────────┘  └──────────────────────┘   │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  OrderRepository (Domain interface)                   │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## Package Structure

```
com.restaurant.orders
├── adapters
│   ├── ports
│   │   ├── in                    # Use case interfaces
│   │   │   ├── CreateOrderUseCase.java
│   │   │   ├── GetOrderUseCase.java
│   │   │   ├── ListOrdersUseCase.java
│   │   │   ├── UpdateOrderStatusUseCase.java
│   │   │   ├── DeleteOrderUseCase.java
│   │   │   ├── AddOrderItemUseCase.java
│   │   │   ├── RemoveOrderItemUseCase.java
│   │   │   └── dto/              # Request/Response DTOs
│   │   │       ├── CreateOrderRequest.java
│   │   │       ├── UpdateStatusRequest.java
│   │   │       ├── AddItemRequest.java
│   │   │       ├── OrderResponse.java
│   │   │       └── ErrorResponse.java
│   │   └── out                   # Output port interfaces
│   │       └── OrderRepositoryPort.java
│   └── drivers
│       ├── in
│       │   └── http
│       │       ├── OrderEndpoints.java    # Functional endpoints
│       │       └── OrderRouter.java       # Route configuration
│       └── out
│           └── mongodb
│               └── OrderMongoRepository.java
├── domain
│   ├── model
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   └── OrderStatus.java
│   ├── service
│   │   └── OrderApplicationService.java
│   └── repository
│       └── OrderRepository.java
├── infrastructure
│   ├── config
│   │   ├── MongoConfig.java
│   │   └── WebFluxConfig.java
│   └── exceptions
│       ├── OrderNotFoundException.java
│       ├── InvalidOrderException.java
│       └── GlobalExceptionHandler.java
└── RestaurantOrdersApplication.java
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create a new order |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/orders?table=5&status=PENDING&dateFrom=2024-01-01T00:00:00&dateTo=2024-12-31T23:59:59` | List orders with filters |
| PATCH | `/api/orders/{id}/status` | Update order status |
| DELETE | `/api/orders/{id}` | Cancel an order |
| POST | `/api/orders/{id}/items` | Add item to order |
| DELETE | `/api/orders/{id}/items/{dishName}` | Remove item from order |

## Domain Model

### Order
- `id`: String (MongoDB generated)
- `tableNumber`: Integer
- `items`: List<OrderItem>
- `totalAmount`: BigDecimal (auto-calculated)
- `status`: OrderStatus (PENDING → PREPARING → SERVED → DELIVERED)
- `createdAt`: LocalDateTime
- `updatedAt`: LocalDateTime

### OrderItem
- `dishName`: String
- `quantity`: Integer
- `unitPrice`: BigDecimal
- `subtotal`: BigDecimal (auto-calculated)

### OrderStatus State Machine
```
PENDING → PREPARING → SERVED → DELIVERED
    ↓                      ↑
    └──── CANCELLED ←──────┘
```

## Building and Running

### Prerequisites
- Java 17 or higher
- MongoDB running on localhost:27017

### Build
```bash
./gradlew build
```

### Run
```bash
./gradlew bootRun
```

### Test
```bash
./gradlew test
```

## Configuration

Edit `src/main/resources/application.yml`:
```yaml
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=restaurant_orders
server.port=8080
```

## Error Handling

All errors return a consistent JSON response:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Order not found with ID: abc123",
  "path": "/api/orders/abc123"
}
```

| Status | Exception | Description |
|--------|-----------|-------------|
| 400 | `WebExchangeBindException` | Validation errors |
| 400 | `IllegalArgumentException` | Invalid arguments |
| 404 | `OrderNotFoundException` | Order not found |
| 409 | `IllegalStateException` | Invalid state transition |
| 500 | `Exception` | Unexpected errors |

## License

MIT
