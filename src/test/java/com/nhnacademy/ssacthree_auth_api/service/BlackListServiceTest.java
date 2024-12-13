package com.nhnacademy.ssacthree_auth_api.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nhnacademy.ssacthree_auth_api.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class BlackListServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private BlackListService blackListService;


    @Test
    void testAddBlackList() {
        // 테스트용 데이터
        String accessToken = "testAccessToken";
        long expirationTime = 60000L; // 1 minute

        // JWTUtil 모의 동작 설정
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(jwtUtil.getExpiredTime(accessToken)).thenReturn(expirationTime);

        // BlackListService의 addBlackList 메서드 호출
        blackListService.addBlackList(accessToken);

        // ValueOperations의 set 메서드가 호출되었는지 검증
        verify(valueOperations, times(1)).set(accessToken, "blacklisted", expirationTime,
            TimeUnit.MILLISECONDS);
    }


    @Test
    void testIsValidToken_validToken() {
        // Mock HttpServletRequest와 쿠키 설정
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = {new Cookie("access-token", "validToken")};
        when(request.getCookies()).thenReturn(cookies);

        // Redis에 키가 존재한다고 가정
        when(redisTemplate.hasKey("validToken")).thenReturn(true);

        boolean result = blackListService.isValidToken(request);

        // 결과 검증
        assertThat(result).isTrue();
        verify(redisTemplate, times(1)).hasKey("validToken");
    }

}
