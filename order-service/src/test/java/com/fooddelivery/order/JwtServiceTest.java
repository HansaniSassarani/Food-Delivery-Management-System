package com.fooddelivery.order;

import com.fooddelivery.order.security.JwtService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {
    @Test
    void generatesAndParsesToken() {
        JwtService jwt = new JwtService("FoodDeliveryJwtSecretKeyMustBeAtLeast256BitsLongForHS256!!", 3600000);
        String token = jwt.generateToken(1L, "d@b.com", "DELIVERY");
        assertEquals("DELIVERY", jwt.parse(token).get("role"));
    }
}
