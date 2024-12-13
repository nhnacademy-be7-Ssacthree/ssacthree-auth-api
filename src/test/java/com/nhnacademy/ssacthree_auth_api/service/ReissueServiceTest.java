package com.nhnacademy.ssacthree_auth_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nhnacademy.ssacthree_auth_api.domain.RefreshToken;
import com.nhnacademy.ssacthree_auth_api.jwt.JWTUtil;
import com.nhnacademy.ssacthree_auth_api.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ReissueServiceTest {

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private ReissueService reissueService;

    @Test
    void testReissueRefreshTokenRefreshTokenIsNull() {

        Cookie[] cookies = new Cookie[1];
        cookies[0] = new Cookie("refresh-token", null);

        when(request.getCookies()).thenReturn(cookies);

        ResponseEntity<?> responseEntity = reissueService.reissueRefreshToken(request, response);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("refresh token null", responseEntity.getBody());
    }

    @Test
    void testReissueRefreshExpired() {
        Cookie refreshTokenCookie = new Cookie("refresh-token", "expiredToken");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        doThrow(ExpiredJwtException.class).when(jwtUtil).isExpired("expiredToken");

        ResponseEntity<?> responseEntity = reissueService.reissueRefreshToken(request, response);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("refresh token expired", responseEntity.getBody());
    }

    @Test
    void testReissueRefreshTokenInvalidCategory() {
        Cookie refreshTokenCookie = new Cookie("refresh-token", "invalidCategoryToken");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        when(jwtUtil.getCategory("invalidCategoryToken")).thenReturn("access");

        ResponseEntity<?> responseEntity = reissueService.reissueRefreshToken(request, response);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("invalid refresh token", responseEntity.getBody());
    }

    @Test
    void testReissueRefreshTokenDoesNotExistInRepository() {
        Cookie refreshTokenCookie = new Cookie("refresh-token", "validRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        when(jwtUtil.getCategory("validRefreshToken")).thenReturn("refresh");
        when(refreshTokenRepository.existsByRefreshToken("validRefreshToken")).thenReturn(false);

        ResponseEntity<?> responseEntity = reissueService.reissueRefreshToken(request, response);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("refresh token does not exist", responseEntity.getBody());
    }

    @Test
    void testReissueRefreshTokenSuccess() {
        Cookie refreshTokenCookie = new Cookie("refresh-token", "validRefreshToken");
        when(request.getCookies()).thenReturn(new Cookie[]{refreshTokenCookie});
        when(jwtUtil.getCategory("validRefreshToken")).thenReturn("refresh");
        when(refreshTokenRepository.existsByRefreshToken("validRefreshToken")).thenReturn(true);
        when(jwtUtil.getMemberLoginId("validRefreshToken")).thenReturn("testUser");
        when(jwtUtil.getRole("validRefreshToken")).thenReturn("USER");
        when(jwtUtil.createJwt(eq("access"), eq("testUser"), eq("USER"), anyLong())).thenReturn(
            "newAccessToken");
        when(jwtUtil.createJwt(eq("refresh"), eq("testUser"), eq("USER"), anyLong())).thenReturn(
            "newRefreshToken");

        ResponseEntity<?> responseEntity = reissueService.reissueRefreshToken(request, response);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(response, times(2)).addCookie(any(Cookie.class)); // AccessToken과 RefreshToken 추가 확인
        verify(refreshTokenRepository).deleteById("testUser");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
}
