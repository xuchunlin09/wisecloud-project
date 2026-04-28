package com.wisecloud.mdm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    // 256-bit key for HMAC-SHA256
    private static final String SECRET = "test-secret-key-for-testing-purposes-only-must-be-at-least-256-bits-long";
    private static final long EXPIRATION_MS = 86400000L; // 24h

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET, EXPIRATION_MS);
    }

    @Test
    void generateToken_returnsNonBlankString() {
        String token = provider.generateToken(1L);
        assertThat(token).isNotBlank();
    }

    @Test
    void validateToken_returnsTrueForValidToken() {
        String token = provider.generateToken(42L);
        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_returnsFalseForGarbageString() {
        assertThat(provider.validateToken("not.a.jwt")).isFalse();
    }

    @Test
    void validateToken_returnsFalseForNull() {
        assertThat(provider.validateToken(null)).isFalse();
    }

    @Test
    void validateToken_returnsFalseForEmptyString() {
        assertThat(provider.validateToken("")).isFalse();
    }

    @Test
    void getUserIdFromToken_returnsCorrectUserId() {
        Long userId = 99L;
        String token = provider.generateToken(userId);
        assertThat(provider.getUserIdFromToken(token)).isEqualTo(userId);
    }

    @Test
    void validateToken_returnsFalseForExpiredToken() {
        // Create a provider with 0ms expiration so token is immediately expired
        JwtTokenProvider shortLived = new JwtTokenProvider(SECRET, 0L);
        String token = shortLived.generateToken(1L);
        assertThat(shortLived.validateToken(token)).isFalse();
    }

    @Test
    void validateToken_returnsFalseForTokenSignedWithDifferentKey() {
        String otherSecret = "another-secret-key-that-is-also-at-least-256-bits-long-for-hmac-sha256";
        JwtTokenProvider otherProvider = new JwtTokenProvider(otherSecret, EXPIRATION_MS);
        String token = otherProvider.generateToken(1L);
        assertThat(provider.validateToken(token)).isFalse();
    }

    @Test
    void getExpirationMs_returnsConfiguredValue() {
        assertThat(provider.getExpirationMs()).isEqualTo(EXPIRATION_MS);
    }
}
