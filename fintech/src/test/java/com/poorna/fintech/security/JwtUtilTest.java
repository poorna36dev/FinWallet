package com.poorna.fintech.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil("test-secret-key-for-jwt-should-be-long-enough", 3_600_000L);

    @Test
    void generateTokenAndExtractUsernameRoundTrip() {
        String token = jwtUtil.generateToken("jdoe");

        assertThat(token).isNotBlank();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("jdoe");
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    void validateTokenReturnsFalseForMalformedToken() {
        assertThat(jwtUtil.validateToken("not-a-valid-token")).isFalse();
    }
}
