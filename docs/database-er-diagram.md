# Database ER (logical)

consumer_db
  consumers 1--* meal_requests

provider_db
  providers 1--* food_items
  providers 1--* quotations  (meal_request_id stored from consumer service)

order_db
  delivery_persons 1--* deliveries
  orders 1--1 deliveries
  notifications (user_id + role)
