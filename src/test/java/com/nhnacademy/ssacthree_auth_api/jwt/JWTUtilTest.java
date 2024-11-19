package com.nhnacademy.ssacthree_auth_api.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class JWTUtilTest {

    private JWTUtil jwtUtil;
    private final String secret = "zwfwaklegnlseljgljnsenfglsejlfljnseljfljnse";
    private final String memberLoginId = "test";
    private final String role = "ROLE_USER";
    private final String category = "refresh";
    private final long expirationTime = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JWTUtil(secret);
    }

    @Test
    void testCreateJwt() {
        String token = jwtUtil.createJwt(category, memberLoginId, role, expirationTime);
        assertNotNull(token);
    }

    @Test
    void testGetMemberLoginId() {
        String token = jwtUtil.createJwt(category, memberLoginId, role, expirationTime);
        String extractedMemberLoginId = jwtUtil.getMemberLoginId(token);
        assertEquals(memberLoginId, extractedMemberLoginId);
    }

    @Test
    void testGetRole() {
        String token = jwtUtil.createJwt(category, memberLoginId, role, expirationTime);
        String extractedRole = jwtUtil.getRole(token);
        assertEquals(role, extractedRole);
    }

    @Test
    void testGetCategory() {
        String token = jwtUtil.createJwt(category, memberLoginId, role, expirationTime);
        String extractedCategory = jwtUtil.getCategory(token);
        assertEquals(category, extractedCategory);
    }

    @Test
    void testIsExpired_NotExpiredToken() {
        String token = jwtUtil.createJwt(category, memberLoginId, role, expirationTime);
        assertFalse(jwtUtil.isExpired(token));
    }

    @Test
    void testIsExpired_ExpiredToken() throws InterruptedException {

        String token = jwtUtil.createJwt(category, memberLoginId, role, 5L); // 1 ms expiration
        Thread.sleep(1000); // wait to ensure token is expired
        Assertions.assertThrows(ExpiredJwtException.class, () -> jwtUtil.isExpired(token));
    }

    @Test
    void testGetExpiredeTime() {
        String token = jwtUtil.createJwt(category, memberLoginId, role, expirationTime);

        Long actualExpirationTime = jwtUtil.getExpiredTime(token);
        assertTrue(
            actualExpirationTime <= expirationTime);
    }
}
