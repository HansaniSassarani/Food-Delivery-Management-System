CREATE DATABASE IF NOT EXISTS order_db;
USE order_db;

CREATE TABLE IF NOT EXISTS delivery_persons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    contact_no VARCHAR(30) NOT NULL,
    password VARCHAR(255) NOT NULL,
    availability_status VARCHAR(30) NOT NULL,
    current_latitude DOUBLE DEFAULT 6.9271,
    current_longitude DOUBLE DEFAULT 79.8612,
    created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    meal_request_id BIGINT NOT NULL,
    quotation_id BIGINT NOT NULL,
    consumer_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    delivery_person_id BIGINT NULL,
    order_status VARCHAR(40) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    estimated_minutes INT,
    created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS deliveries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    delivery_person_id BIGINT NULL,
    pickup_latitude DOUBLE,
    pickup_longitude DOUBLE,
    delivery_latitude DOUBLE,
    delivery_longitude DOUBLE,
    current_latitude DOUBLE,
    current_longitude DOUBLE,
    delivery_status VARCHAR(40) NOT NULL,
    CONSTRAINT fk_delivery_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role VARCHAR(30) NOT NULL,
    message VARCHAR(500) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL
);
