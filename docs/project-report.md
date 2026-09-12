# Project report

## Idea

HotRoute is a food-delivery marketplace with a ride-booking delivery model. A customer posts a meal request. Available providers quote. The customer accepts one quote. Available riders receive the job. The selected rider picks up food and the customer tracks GPS on OpenStreetMap.

## Members

1. Consumer service — registration, JWT, profile, meal requests
2. Provider service — food items, quotations, capacity
3. Order service — orders, delivery availability, tracking, notifications

Shared: frontend, MySQL, Docker, Jenkins, Postman, GitHub.

## Stack

Java 17, Spring Boot 3.3, Spring Security + JWT + BCrypt, MySQL 8, HTML/CSS/JS, Leaflet, Docker Compose, Jenkins.

## Architecture

Frontend → REST → Consumer / Provider / Order microservices → separate MySQL schemas.

Order service calls the other two when a quotation is accepted, then fans out notifications to available riders.

## Testing

Import `postman/` collections. Sequence: register consumer → login → meal request → register provider → food item → quote → accept quotation → register rider → accept delivery → GPS → delivered.
