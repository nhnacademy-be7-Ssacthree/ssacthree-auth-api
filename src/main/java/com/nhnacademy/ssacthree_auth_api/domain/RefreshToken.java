package com.nhnacademy.ssacthree_auth_api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash("refreshToken")
@AllArgsConstructor
public class RefreshToken {

    @Id
    private String memberLoginId;

    @Indexed    // refreshToken으로 조회하기 위함임.
    private String refreshToken;

    @TimeToLive
    private long expiration;

}
