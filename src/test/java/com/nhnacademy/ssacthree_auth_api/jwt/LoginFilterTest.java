package com.nhnacademy.ssacthree_auth_api.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ssacthree_auth_api.domain.Member;
import com.nhnacademy.ssacthree_auth_api.domain.RefreshToken;
import com.nhnacademy.ssacthree_auth_api.dto.LoginRequestDto;
import com.nhnacademy.ssacthree_auth_api.repository.MemberRepository;
import com.nhnacademy.ssacthree_auth_api.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@ExtendWith(MockitoExtension.class)
class LoginFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private LoginFilter loginFilter;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        loginFilter = new LoginFilter(authenticationManager, jwtUtil, objectMapper,
            refreshTokenRepository, memberRepository);
    }

    @Test
    @DisplayName("attemptAuthentication - 성공적인 인증 요청")
    void attemptAuthentication_Success() throws Exception {
        // Arrange (준비)
        MockHttpServletRequest request = new MockHttpServletRequest();
        LoginRequestDto loginRequestDto = new LoginRequestDto("testUser", "testPassword");

        // 요청 JSON 데이터 설정
        String requestBody = objectMapper.writeValueAsString(loginRequestDto);
        request.setContent(requestBody.getBytes());
        request.setContentType("application/json");

        // Mock Authentication
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(
            "testUser", "testPassword", Collections.emptyList());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mockAuth);

        // Act (실행)
        Authentication result = loginFilter.attemptAuthentication(request,
            new MockHttpServletResponse());

        // Assert (검증)
        assertNotNull(result, "Authentication 결과가 null이면 안 됩니다.");
        assertEquals("testUser", result.getPrincipal(), "Principal은 'testUser'여야 합니다.");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }


    @Test
    @DisplayName("successfulAuthentication - 성공적인 인증")
    void successfulAuthentication_Success() {
        // Mock Response
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Mock Authentication
        User user = new User("testUser", "testPassword",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication authResult = new UsernamePasswordAuthenticationToken(user, null,
            user.getAuthorities());

        // Mock Token Generation
        when(jwtUtil.createJwt(any(), any(), any(), any())).thenReturn("mockAccessToken",
            "mockRefreshToken");

        // Mock Member Repository
        Member member = new Member();
        member.setMemberLoginId("testUser");
        when(memberRepository.findByMemberLoginId("testUser")).thenReturn(member);

        // Call Method
        loginFilter.successfulAuthentication(new MockHttpServletRequest(), response,
            mock(FilterChain.class), authResult);

        // Verify Cookies and Member Update
        assertNotNull(response.getCookies());
        assertTrue(response.getCookies().length > 0);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(memberRepository).save(member);
    }

    @Test
    @DisplayName("unsuccessfulAuthentication - 실패한 인증")
    void unsuccessfulAuthentication_Success() {
        // Mock Response
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Call Method
        loginFilter.unsuccessfulAuthentication(new MockHttpServletRequest(), response, mock(
            AuthenticationException.class));

        // Verify Response Status
        assertEquals(401, response.getStatus());
    }
}
