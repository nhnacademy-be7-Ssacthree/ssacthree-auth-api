package com.nhnacademy.ssacthree_auth_api.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ssacthree_auth_api.domain.Member;
import com.nhnacademy.ssacthree_auth_api.domain.RefreshToken;
import com.nhnacademy.ssacthree_auth_api.dto.LoginRequestDto;
import com.nhnacademy.ssacthree_auth_api.exception.IllegalFormatException;
import com.nhnacademy.ssacthree_auth_api.exception.SleepMemberException;
import com.nhnacademy.ssacthree_auth_api.exception.WithdrawMemberException;
import com.nhnacademy.ssacthree_auth_api.repository.MemberRepository;
import com.nhnacademy.ssacthree_auth_api.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    private static final long ACCESS_TOKEN_EXPIRED = 30 * 60 * 1000L; // 30분
    private static final long REFRESH_TOKEN_EXPIRED = 120 * 60 * 1000L; // 2시간
    private static final String MEMBER_LOGIN_ID = "memberLoginId";
    private static final String PASSWORD_PARAMETER = "memberPassword";

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
        ObjectMapper objectMapper, RefreshTokenRepository refreshRepository,
        MemberRepository memberRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.refreshTokenRepository = refreshRepository;
        this.memberRepository = memberRepository;
        setUsernameParameter(MEMBER_LOGIN_ID);
        setPasswordParameter(PASSWORD_PARAMETER);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {

        LoginRequestDto loginRequestDto = null;
        try {
            loginRequestDto = objectMapper.readValue(request.getInputStream(),
                LoginRequestDto.class);
        } catch (IOException e) {
            throw new IllegalFormatException("잘못된 형식의 요청입니다.");
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            loginRequestDto.getLoginId(), loginRequestDto.getPassword(), null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authentication) {

        //유저 정보 불러옴.
        String memberLoginId = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        //토큰을 생성 하는 방법
        String access = jwtUtil.createJwt("access", memberLoginId, role, ACCESS_TOKEN_EXPIRED);
        String refresh = jwtUtil.createJwt("refresh", memberLoginId, role, REFRESH_TOKEN_EXPIRED);
        addRefreshToken(memberLoginId, refresh, REFRESH_TOKEN_EXPIRED);
        //응답 설정
        response.addCookie(createCookie("access-token", access, ACCESS_TOKEN_EXPIRED));
        response.addCookie(createCookie("refresh-token", refresh, REFRESH_TOKEN_EXPIRED));

        // 마지막 로그인 날짜 업데이트
        Member member = memberRepository.findByMemberLoginId(memberLoginId);
        if (member != null) {
            member.setMemberLastLoginAt(LocalDateTime.now());
            memberRepository.save(member);
        }

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed) {
        log.info("{}", failed.getMessage());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        int statusCode = 401;
        String message = failed.getMessage();
        if (failed.getCause() instanceof WithdrawMemberException) {
            statusCode = 403;
            response.setStatus(403);
        } else if (failed.getCause() instanceof SleepMemberException) {
            statusCode = 423;
            response.setStatus(423);
        } else {
            response.setStatus(401);
        }

        try {
            String jsonResponse = String.format("{\"statusCode\": %d, \"message\": \"%s\"}",
                statusCode,
                message);
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            log.error("IOException", e);
        }
    }

    private Cookie createCookie(String key, String value, long expiredMs) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge((int) expiredMs / 1000);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshToken(String memberLoginId, String refresh, long expiredMs) {
        // 이렇게 redis에 저장. ttl 땜시 유효시간 지나면 알아서 레디스에서 삭제됨.
        RefreshToken refreshToken = new RefreshToken(memberLoginId, refresh, expiredMs);
        refreshTokenRepository.save(refreshToken);
    }

}
