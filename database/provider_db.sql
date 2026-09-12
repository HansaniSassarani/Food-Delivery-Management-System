CREATE DATABASE IF NOT EXISTS provider_db;
USE provider_db;

CREATE TABLE IF NOT EXISTS providers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    contact_no VARCHAR(30) NOT NULL,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    capacity INT NOT NULL DEFAULT 10,
    availability_status VARCHAR(30) NOT NULL,
    latitude DOUBLE DEFAULT 6.9147,
    longitude DOUBLE DEFAULT 79.8728,
    created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS food_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    provider_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    available_quantity INT NOT NULL,
    CONSTRAINT fk_food_provider FOREIGN KEY (provider_id) REFERENCES providers(id)
);

CREATE TABLE IF NOT EXISTS quotations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    meal_request_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    estimated_time INT NOT NULL,
    notes VARCHAR(400),
    status VARCHAR(40) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_quote_provider FOREIGN KEY (provider_id) REFERENCES providers(id)
);
