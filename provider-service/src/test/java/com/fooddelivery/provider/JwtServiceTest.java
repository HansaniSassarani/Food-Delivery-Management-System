package com.fooddelivery.provider;

import com.fooddelivery.provider.security.JwtService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {
    @Test
    void generatesAndParsesToken() {
        JwtService jwt = new JwtService("FoodDeliveryJwtSecretKeyMustBeAtLeast256BitsLongForHS256!!", 3600000);
        String token = jwt.generateToken(3L, "p@b.com", "PROVIDER");
        assertEquals("PROVIDER", jwt.parse(token).get("role"));
    }
}
