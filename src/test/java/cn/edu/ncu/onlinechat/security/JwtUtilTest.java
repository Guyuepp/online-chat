package cn.edu.ncu.onlinechat.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("test-secret-key-at-least-32-bytes-long!!");
        props.setExpiration(7_200_000L);
        props.setHeader("Authorization");
        props.setPrefix("Bearer ");
        jwtUtil = new JwtUtil(props);
    }

    @Test
    void generateTokenShouldIncludeJti() {
        String token = jwtUtil.generateToken(1L, "testuser");

        String jti = jwtUtil.parseJti(token);
        assertThat(jti).isNotBlank();
        assertThat(jti).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    void generateTokenShouldIncludeUserIdAndUsername() {
        String token = jwtUtil.generateToken(42L, "alice");

        assertThat(jwtUtil.parseUserId(token)).isEqualTo(42L);
        assertThat(jwtUtil.parseUsername(token)).isEqualTo("alice");
    }

    @Test
    void parseExpirationShouldReturnFutureDate() {
        String token = jwtUtil.generateToken(1L, "testuser");

        Date expiration = jwtUtil.parseExpiration(token);
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    void isValidShouldReturnTrueForNewToken() {
        String token = jwtUtil.generateToken(1L, "testuser");

        assertThat(jwtUtil.isValid(token)).isTrue();
    }

    @Test
    void isValidShouldReturnFalseForTamperedToken() {
        String token = jwtUtil.generateToken(1L, "testuser");
        String tampered = token.substring(0, token.length() - 3) + "xxx";

        assertThat(jwtUtil.isValid(tampered)).isFalse();
    }

    @Test
    void eachTokenShouldHaveUniqueJti() {
        String token1 = jwtUtil.generateToken(1L, "testuser");
        String token2 = jwtUtil.generateToken(1L, "testuser");

        assertThat(jwtUtil.parseJti(token1)).isNotEqualTo(jwtUtil.parseJti(token2));
    }
}
