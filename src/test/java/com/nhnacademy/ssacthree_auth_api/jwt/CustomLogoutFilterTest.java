package com.nhnacademy.ssacthree_auth_api.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nhnacademy.ssacthree_auth_api.repository.RefreshTokenRepository;
import com.nhnacademy.ssacthree_auth_api.service.BlackListService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class CustomLogoutFilterTest {

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private BlackListService blackListService;

    @InjectMocks
    private CustomLogoutFilter customLogoutFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = (request, response) -> {
        };
    }

    @Test
    @DisplayName("doFilter - 로그아웃 요청이 아닌 경우 필터 체인 실행")
    void doFilter_NotLogoutRequest() throws IOException, ServletException {
        request.setRequestURI("/api/auth/not-logout");
        request.setMethod("GET");

        customLogoutFilter.doFilter(request, response, filterChain);

        // Verify that filter chain is executed
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("doFilter - 로그아웃 요청이지만 POST가 아닌 경우 필터 체인 실행")
    void doFilter_LogoutRequestButNotPost() throws IOException, ServletException {
        request.setRequestURI("/api/auth/logout");
        request.setMethod("GET");

        customLogoutFilter.doFilter(request, response, filterChain);

        // Verify that filter chain is executed
        assertEquals(200, response.getStatus());
    }


    @Test
    @DisplayName("doFilter - 로그아웃 요청, Refresh 토큰이 유효하지 않음")
    void doFilter_LogoutRequestInvalidRefreshToken() throws IOException, ServletException {
        request.setRequestURI("/api/auth/logout");
        request.setMethod("POST");
        request.setCookies(new Cookie("refresh-token", "invalidToken"));

        when(jwtUtil.getCategory(any())).thenReturn("invalid");

        customLogoutFilter.doFilter(request, response, filterChain);

        // Verify that response status is 400
        assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("doFilter - 로그아웃 요청, Refresh 토큰이 DB에 없음")
    void doFilter_LogoutRequestRefreshTokenNotInDB() throws IOException, ServletException {
        request.setRequestURI("/api/auth/logout");
        request.setMethod("POST");
        request.setCookies(new Cookie("refresh-token", "validRefreshToken"));

        when(jwtUtil.getCategory(any())).thenReturn("refresh");
        when(refreshTokenRepository.existsByRefreshToken(any())).thenReturn(false);

        customLogoutFilter.doFilter(request, response, filterChain);

        // Verify that response status is 400
        assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("doFilter - 로그아웃 요청, 성공적인 로그아웃")
    void doFilter_LogoutRequestSuccess() throws IOException, ServletException {
        request.setRequestURI("/api/auth/logout");
        request.setMethod("POST");
        request.setCookies(new Cookie("refresh-token", "validRefreshToken"),
            new Cookie("access-token", "validAccessToken"));

        when(jwtUtil.getCategory(any())).thenReturn("refresh");
        when(refreshTokenRepository.existsByRefreshToken(any())).thenReturn(true);
        when(jwtUtil.getMemberLoginId(any())).thenReturn("testUser");

        customLogoutFilter.doFilter(request, response, filterChain);

        // Verify that tokens are removed and response status is 200
        verify(refreshTokenRepository).deleteById(any());
        verify(blackListService).addBlackList(any());
        assertEquals(200, response.getStatus());
    }
}