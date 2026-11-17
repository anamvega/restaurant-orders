# Restaurant Orders Microservice

## Overview

A reactive microservice for managing restaurant **tables (mesas)** and **orders**, built with **Spring WebFlux**, **Project Reactor**, and **MongoDB** following **Hexagonal Architecture** (Ports & Adapters).

## Tech Stack

- **Java 17**
- **Spring Boot 3.4**
- **Spring WebFlux** (reactive, non-blocking; functional endpoints on Netty)
- **Project Reactor** (`Mono` / `Flux`)
- **MongoDB 7** (reactive driver)
- **Gradle 8.14** (wrapper included)
- **Lombok**
- **JUnit 5 + Mockito + Reactor Test**
- **Docker Compose** (local MongoDB)

## Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                  HTTP Adapters (driving / in)                │
│   OrderRouter + OrderEndpoints     MesaRouter + MesaEndpoints │
└───────────────────────────────┬──────────────────────────────┘
                                │
┌───────────────────────────────▼──────────────────────────────┐
│                  Application Services                        │
│        OrderApplicationService   MesaApplicationService      │
└───────────────┬───────────────────────────────┬──────────────┘
                │                               │
┌───────────────▼──────────────┐  ┌─────────────▼──────────────┐
│         Domain Model         │  │  Outbound Ports            │
│  Order, OrderItem,           │  │  OrderRepositoryPort       │
│  OrderStatus (state machine) │  │  MesaRepositoryPort        │
│  Mesa, MesaStatus            │  └─────────────┬──────────────┘
└──────────────────────────────┘                │
                                  ┌─────────────▼──────────────┐
                                  │ MongoDB Adapters (driven)  │
                                  │ OrderMongoRepository       │
                                  │ MesaMongoRepository        │
                                  └────────────────────────────┘
```

Request flow: **Router → Endpoints → ApplicationService → RepositoryPort → MongoDB adapter**.

## Package Structure

```
com.restaurant.orders
├── adapters
│   ├── drivers
│   │   ├── in/http                   # Functional routers and handlers
│   │   │   ├── OrderRouter.java / OrderEndpoints.java
│   │   │   ├── MesaRouter.java  / MesaEndpoints.java
│   │   │   └── MesaCreateRequest.java, MesaStatusRequest.java
│   │   └── out/mongodb               # ReactiveMongoTemplate implementations
│   │       ├── OrderMongoRepository.java
│   │       └── MesaMongoRepository.java
│   └── ports
│       ├── in
│       │   ├── AddOrderItemCommand.java
│       │   ├── ListOrdersFilter.java
│       │   └── dto/                  # Order request/response DTOs, ErrorResponse
│       └── out
│           ├── OrderRepositoryPort.java
│           └── MesaRepositoryPort.java
├── domain
│   ├── model                         # Order, OrderItem, OrderStatus, Mesa, MesaStatus
│   ├── service                       # OrderApplicationService, MesaApplicationService
│   ├── repository                    # OrderRepository (Spring Data, currently unused)
│   └── util
│       ├── ErrorMessages.java        # All error message texts/templates
│       └── ValidationUtils.java      # Reusable input validations
├── infrastructure
│   ├── config                        # MongoConfig, WebFluxConfig
│   └── exceptions
│       ├── GlobalExceptionHandler.java
│       ├── ErrorResponseUtils.java   # Builds the JSON error response
│       ├── OrderNotFoundException.java
│       ├── MesaNotFoundException.java
│       ├── MesaAlreadyExistsException.java
│       └── InvalidOrderException.java
└── RestaurantOrdersApplication.java
```

## API Endpoints

Base URL: `http://localhost:8081/api`

### Orders

| Method | Endpoint | Description | Success |
|--------|----------|-------------|---------|
| POST | `/orders` | Create an order | 200 |
| GET | `/orders/{id}` | Get order by ID | 200 |
| GET | `/orders?table=5&status=PENDING&dateFrom=2024-01-01T00:00:00&dateTo=2024-12-31T23:59:59` | List orders (all filters optional) | 200 |
| PATCH | `/orders/{id}/status` | Change order status | 200 |
| DELETE | `/orders/{id}` | Cancel an order (sets `CANCELLED`, does not delete) | 200 |
| POST | `/orders/{id}/items` | Add an item (same dish name adds to quantity) | 200 |
| DELETE | `/orders/{id}/items/{dishName}` | Remove an item | 204 |

```jsonc
// POST /orders
{ "tableNumber": 5, "items": [ { "dishName": "Pasta", "quantity": 2, "unitPrice": 12.50 } ] }

// PATCH /orders/{id}/status
{ "status": "PREPARING" }

// POST /orders/{id}/items
{ "dishName": "Soda", "quantity": 1, "unitPrice": 3.00 }
```

### Tables (mesas)

| Method | Endpoint | Description | Success |
|--------|----------|-------------|---------|
| POST | `/mesas` | Create a table | 201 |
| GET | `/mesas` | List tables (optional `?status=AVAILABLE`) | 200 |
| GET | `/mesas/available` | List available tables | 200 |
| GET | `/mesas/{id}` | Get table by ID | 200 |
| PATCH | `/mesas/{id}/status` | Change table status | 200 |
| DELETE | `/mesas/{id}` | Delete a table | 204 |

```jsonc
// POST /mesas  — number and capacity must be > 0; number must be unique
{ "number": 5, "capacity": 4 }

// PATCH /mesas/{id}/status  — case-insensitive
{ "status": "OCCUPIED" }
```

> `/mesas/available` must stay declared **before** `/mesas/{id}` in `MesaRouter`: functional routes match in declaration order, so `{id}` would otherwise capture `"available"`.

## Domain Model

### Order
- `id`: String (generated by MongoDB)
- `tableNumber`: Integer (> 0)
- `items`: List<OrderItem> (at least one)
- `totalAmount`: BigDecimal (recalculated on every change)
- `status`: OrderStatus (starts as `PENDING`)
- `createdAt`, `updatedAt`: LocalDateTime

### OrderItem
- `dishName`: String
- `quantity`: Integer (> 0)
- `unitPrice`: BigDecimal (> 0)
- `subtotal`: BigDecimal (auto-calculated)

### OrderStatus state machine
```
PENDING ──► PREPARING ──► SERVED ──► DELIVERED
   │            │
   └────────────┴──► CANCELLED
```
- `DELIVERED` and `CANCELLED` are terminal: items can no longer be added or removed.

### Mesa
- `id`: String (generated by MongoDB)
- `number`: Integer (> 0, unique)
- `capacity`: Integer (> 0)
- `status`: MesaStatus (starts as `AVAILABLE`)
- `createdAt`, `updatedAt`: LocalDateTime

### MesaStatus
`AVAILABLE`, `OCCUPIED`, `RESERVED`, `CLEANING`. A table in `CLEANING` cannot be set to `OCCUPIED` or `RESERVED`; it must go to `AVAILABLE` first.

## Building and Running

### Prerequisites
- Java 17
- Docker Desktop (for the local MongoDB), or a MongoDB instance on `localhost:27017`

### 1. Start MongoDB
```bash
docker compose up -d
```
This starts `mongo:7` as `restaurant-orders-mongo` on port `27017` with a persistent volume. If the app logs `MongoSocketOpenException: Exception opening socket`, MongoDB is not running.

### 2. Run the app
```bash
gradlew.bat bootRun        # Windows
```
The API starts on **port 8081**.

### Build / Test
```bash
gradlew.bat build
gradlew.bat test
```

> Only the Windows wrapper (`gradlew.bat`) is committed. On Linux/macOS, generate the Unix script with `gradle wrapper` or use a local Gradle installation.

## Configuration

`src/main/resources/application.yml`:
```yaml
server:
  port: 8081

spring:
  application:
    name: restaurant-orders
  data:
    mongodb:
      host: localhost
      port: 27017
      database: restaurant_orders

logging:
  level:
    com.restaurant.orders: DEBUG
```

## Error Handling

All errors return the same JSON shape, built by `ErrorResponseUtils`:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Mesa not found with ID: abc123",
  "path": "/api/mesas/abc123"
}
```

| Status | Exception | When |
|--------|-----------|------|
| 400 | `IllegalArgumentException` | Invalid input (non-positive values, unknown status, invalid order status transition, bad date format, item not in order) |
| 400 | `WebExchangeBindException` | Bean validation errors |
| 404 | `OrderNotFoundException` | Order ID does not exist |
| 404 | `MesaNotFoundException` | Table ID does not exist |
| 409 | `MesaAlreadyExistsException` | Table number already in use |
| 409 | `IllegalStateException` | Modifying a terminal order, or occupying/reserving a table in `CLEANING` |
| 500 | `Exception` | Unexpected errors (e.g. MongoDB unavailable) |

Message texts live in `domain/util/ErrorMessages.java`.

## Known Limitations

- Bean validation annotations on DTOs (`@NotNull`, `@Min`, …) are not executed by the functional endpoints; validation happens in the services and domain model.
- Orders are not linked to tables: creating an order does not check that the table exists or change its status.
- The duplicate table-number check is not atomic (no unique index on `mesas.number`).
- List endpoints are not paginated.

## License

MIT
