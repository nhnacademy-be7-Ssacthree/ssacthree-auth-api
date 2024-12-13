package com.nhnacademy.ssacthree_auth_api.jwt;

import static com.nhnacademy.ssacthree_auth_api.jwt.LoginFilter.getAuthentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ssacthree_auth_api.domain.RefreshToken;
import com.nhnacademy.ssacthree_auth_api.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Iterator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AdminLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final long ACCESS_TOKEN_EXPIRED = 30 * 60 * 1000L; // 30분
    private static final long REFRESH_TOKEN_EXPIRED = 120 * 60 * 1000L; // 2시간
    private static final String USERNAME_PARAMETER = "memberLoginId";
    private static final String PASSWORD_PARAMETER = "memberPassword";

    public AdminLoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
        ObjectMapper objectMapper, RefreshTokenRepository refreshRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.refreshTokenRepository = refreshRepository;
        setUsernameParameter(USERNAME_PARAMETER);
        setPasswordParameter(PASSWORD_PARAMETER);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {

        return getAuthentication(request, objectMapper, authenticationManager);
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
        addRefreshToken(memberLoginId, refresh);
        //응답 설정
        response.addCookie(createCookie("access-token", access, ACCESS_TOKEN_EXPIRED));
        response.addCookie(createCookie("refresh-token", refresh, REFRESH_TOKEN_EXPIRED));


    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }

    private Cookie createCookie(String key, String value, long expiredMs) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge((int) expiredMs / 1000);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshToken(String memberLoginId, String refresh) {
        // 이렇게 redis에 저장. ttl 땜시 유효시간 지나면 알아서 레디스에서 삭제됨.
        RefreshToken refreshToken = new RefreshToken(memberLoginId, refresh,
            AdminLoginFilter.REFRESH_TOKEN_EXPIRED);
        refreshTokenRepository.save(refreshToken);
    }

}

