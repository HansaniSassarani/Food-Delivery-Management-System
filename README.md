# Food Delivery Management System

PickMe-style marketplace for food delivery. Customers post meal requests, providers submit quotations, riders accept deliveries, and GPS tracking is shown on OpenStreetMap.

## Architecture

- **consumer-service** (port 8081) — Member 1: register, JWT login, profile, meal requests
- **provider-service** (port 8082) — Member 2: provider profile, food items, quotations, capacity
- **order-service** (port 8083) — Member 3: accept quotation, orders, delivery, notifications
- **frontend** — HTML / CSS / JavaScript + Leaflet maps
- **MySQL** — `consumer_db`, `provider_db`, `order_db`

## Run with Docker

```bash
docker compose up --build
```

Open http://localhost:8080

## Run locally

1. Start MySQL 8 and create the three databases (see `database/`).
2. Default DB user/password: `root` / `root`.
3. Run each Spring Boot app from its folder (`mvn spring-boot:run`).
4. Open `frontend/index.html` via a static server, or use Docker only for nginx.

## Demo flow

1. Register a **customer**, a **provider**, and a **rider**.
2. Customer creates a meal request.
3. Provider (status AVAILABLE) opens meal requests and submits a quotation.
4. Customer accepts the quotation — an order and delivery request are created.
5. Rider accepts the job, shares GPS, marks picked up / on the way / delivered.
6. Customer tracks the rider on the map.

## Git branches (group)

```
main
develop
feature/member1-consumer
feature/member2-provider
feature/member3-order
```

## Auth

JWT is issued on login. Send:

```
Authorization: Bearer <token>
```

The same signing secret is used by all three services so a customer token works on order-service.

Maps use OpenStreetMap + Leaflet (no Google API key).
