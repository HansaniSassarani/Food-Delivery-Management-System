CREATE DATABASE IF NOT EXISTS consumer_db;
USE consumer_db;

CREATE TABLE IF NOT EXISTS consumers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    organization_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    contact_no VARCHAR(30) NOT NULL,
    password VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE DEFAULT 6.9271,
    longitude DOUBLE DEFAULT 79.8612,
    created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS meal_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    consumer_id BIGINT NOT NULL,
    request_date DATE NOT NULL,
    quantity INT NOT NULL,
    delivery_date DATETIME NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    food_description VARCHAR(500) NOT NULL,
    status VARCHAR(40) NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_meal_consumer FOREIGN KEY (consumer_id) REFERENCES consumers(id)
);
