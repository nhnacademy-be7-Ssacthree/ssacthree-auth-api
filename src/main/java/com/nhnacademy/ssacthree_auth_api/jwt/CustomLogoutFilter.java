package com.nhnacademy.ssacthree_auth_api.jwt;

import com.nhnacademy.ssacthree_auth_api.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String requestURI = request.getRequestURI();

        // uri가 Logout이 아니면 다음 필터로
        if (!requestURI.equals("/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        // POST가 아니면 다음 필터로
        if (!request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키 가져와서 refresh 토큰 있는지 확인함.
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("refresh-token")) {
                refresh = cookie.getValue();
            }
        }

        // 없으면 400에러 응답.
        if(refresh == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        // null이 아니야? 그럼 유효기간 확인

        try {
            jwtUtil.isExpired(refresh);
        }
        catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh인지 확인하라네 근데 위에서 해야하는거 아닌가..? 쨋든
        String category = jwtUtil.getCategory(refresh);

        if(!category.equals("refresh")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //아니면 또 아래 로직 수행 일단 DB에 refresh 토큰이 있는지 검사
        if(!refreshTokenRepository.existsByRefreshToken(refresh)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //토큰이 있다면 로그아웃을 진행함

        // 1. 토큰을 제거해야 reissue가 안됨. 근데.. refresh 만으로는 토큰을 지울수가 없어서
        // 우선은 memberLoginId를 가지고 지움...
        refreshTokenRepository.deleteById(jwtUtil.getMemberLoginId(refresh));

        // 2. 쿠키 터뜨림.
        response.addCookie(cookieDestroyer("refresh-token"));
        response.addCookie(cookieDestroyer("access-token"));
        response.setStatus(HttpServletResponse.SC_OK);



    }

    private Cookie cookieDestroyer(String cookieName) {

        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}
