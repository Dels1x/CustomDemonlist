package ua.delsix.security;

import org.junit.jupiter.api.Test;

class JwtUtilTest {

    @Test
    void generateToken() {
        JwtUtil jwtUtil = new JwtUtil();
        System.out.println(jwtUtil.generateToken("delsix4"));
    }
}