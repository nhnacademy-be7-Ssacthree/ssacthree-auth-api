package com.nhnacademy.ssacthree_auth_api.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ssacthree_auth_api.domain.CustomUserDetails;
import com.nhnacademy.ssacthree_auth_api.dto.LoginRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final String usernameParameter = "memberLoginId";
    private final String passwordParameter = "memberPassword";
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        setUsernameParameter(usernameParameter);
        setPasswordParameter(passwordParameter);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        LoginRequestDto loginRequestDto = null;
        try {
            loginRequestDto = objectMapper.readValue(request.getInputStream(),
                LoginRequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getMemberLoginId(), loginRequestDto.getMemberPassword(),null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,Authentication authentication) {

//        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
//        String memberLoginId = customUserDetails.getUsername();
//
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority auth = iterator.next();
//
//        String role = auth.getAuthority();
//
//        String token = jwtUtil.createJwt(memberLoginId, role, 600*600*10L);
//
//        String cookieValue = token;
//        Cookie cookie = new Cookie("access-token",cookieValue);
//        cookie.setMaxAge(600 * 600 * 10);
//        cookie.setHttpOnly(true);
//        response.addCookie(cookie);
//        response.addHeader("Authorization", "Bearer " + token);

        //유저 정보 불러옴.
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        //토큰을 생성 하는 방법
        String access = jwtUtil.createJwt("access",username,role,600000L);
        String refresh = jwtUtil.createJwt("refresh",username,role,86500000L);

        //응답 설정
        response.addCookie(createCookie("access-token",access,600000L));
        response.addCookie(createCookie("refresh-token",refresh,86500000L));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }

    private Cookie createCookie(String key, String value, long expired) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge((int)expired);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }




}
