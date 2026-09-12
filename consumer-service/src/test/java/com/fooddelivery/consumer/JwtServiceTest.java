package com.fooddelivery.consumer;

import com.fooddelivery.consumer.security.JwtService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {
    @Test
    void generatesAndParsesToken() {
        JwtService jwt = new JwtService("FoodDeliveryJwtSecretKeyMustBeAtLeast256BitsLongForHS256!!", 3600000);
        String token = jwt.generateToken(9L, "a@b.com", "CONSUMER");
        assertEquals("a@b.com", jwt.parse(token).getSubject());
        assertEquals(9, ((Number) jwt.parse(token).get("uid")).intValue());
    }
}
