# System architecture

```
Frontend (HTML/CSS/JS + Leaflet)
        |
        +-- consumer-service :8081 --> consumer_db
        +-- provider-service :8082 --> provider_db
        +-- order-service    :8083 --> order_db
                                      |
                                      +-- delivery management
                                      +-- notifications
                                      +-- map coordinates
```
