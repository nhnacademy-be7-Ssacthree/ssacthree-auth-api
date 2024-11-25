package com.nhnacademy.ssacthree_auth_api.service;

import com.nhnacademy.ssacthree_auth_api.domain.Member;
import com.nhnacademy.ssacthree_auth_api.domain.RefreshToken;
import com.nhnacademy.ssacthree_auth_api.exception.MemberNotFoundException;
import com.nhnacademy.ssacthree_auth_api.jwt.JWTUtil;
import com.nhnacademy.ssacthree_auth_api.repository.MemberRepository;
import com.nhnacademy.ssacthree_auth_api.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaycoService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    private final long accessTokenExpired = 30 * 60 * 1000L; // 30분
    private final long refreshTokenExpired = 120 * 60 * 1000L; // 2시간


    public void paycoLogin(String paycoIdNo, HttpServletResponse response) {
        Member member = memberRepository.findByPaycoIdNumber(paycoIdNo)
            .orElseThrow(() -> new MemberNotFoundException("연동된 계정을 찾을 수 없습니다."));

        String role = "ROLE_USER";

        //토큰을 생성 하는 방법
        String access = jwtUtil.createJwt("access", member.getMemberLoginId(), role,
            accessTokenExpired);
        String refresh = jwtUtil.createJwt("refresh", member.getMemberLoginId(), role,
            refreshTokenExpired);
        addRefreshToken(member.getMemberLoginId(), refresh, refreshTokenExpired);
        //응답 설정
        response.addCookie(createCookie("access-token", access, accessTokenExpired));
        response.addCookie(createCookie("refresh-token", refresh, refreshTokenExpired));

        member.setMemberLastLoginAt(LocalDateTime.now());
        memberRepository.save(member);

    }

    public void paycoConnection(String paycoIdNo, String memberLoginId) {
        Member member = memberRepository.findByMemberLoginId(memberLoginId);

        member.setPaycoIdNumber(paycoIdNo);
        memberRepository.save(member);
    }

    private Cookie createCookie(String key, String value, long expiredMs) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge((int) expiredMs / 1000);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshToken(String memberLoginId, String refresh, long expiredMs) {
        // 이렇게 redis에 저장. ttl 땜시 유효시간 지나면 알아서 레디스에서 삭제됨.
        RefreshToken refreshToken = new RefreshToken(memberLoginId, refresh, expiredMs);
        refreshTokenRepository.save(refreshToken);
    }

}