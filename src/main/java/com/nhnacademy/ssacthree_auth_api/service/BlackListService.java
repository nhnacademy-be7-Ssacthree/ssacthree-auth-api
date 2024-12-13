package com.nhnacademy.ssacthree_auth_api.service;

import com.nhnacademy.ssacthree_auth_api.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlackListService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JWTUtil jwtUtil;

    public void addBlackList(String accessToken) {
        Long expiredMs = jwtUtil.getExpiredTime(accessToken);
        redisTemplate.opsForValue()
            .set(accessToken, "blacklisted", expiredMs, TimeUnit.MILLISECONDS);
    }

    public boolean isValidToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String accessToken = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("access-token")) {
                accessToken = cookie.getValue();
                break;
            }
        }

        return Boolean.TRUE.equals(redisTemplate.hasKey(accessToken));


    }

}
