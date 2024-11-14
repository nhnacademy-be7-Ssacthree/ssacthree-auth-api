package com.nhnacademy.ssacthree_auth_api.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlackListService {

    private final RedisTemplate<String, String> redisTemplate;

    public void addBlackList(String accessToken, Long expired) {
        redisTemplate.opsForValue().set(accessToken, "blacklisted", expired, TimeUnit.MILLISECONDS);
    }

    public boolean isValid(String accessToken) {
        return redisTemplate.hasKey(accessToken);
    }

}
