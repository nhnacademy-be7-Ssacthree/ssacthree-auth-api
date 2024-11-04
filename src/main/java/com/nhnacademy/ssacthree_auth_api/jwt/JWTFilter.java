package com.nhnacademy.ssacthree_auth_api.jwt;

import com.nhnacademy.ssacthree_auth_api.domain.CustomUserDetails;
import com.nhnacademy.ssacthree_auth_api.domain.Member;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        // 쿠키에서 access키에 담긴 토큰을 꺼냄
        String accessToken = null;

        // 쿠키가 비어있다면 담 필터로 넘기지 말고.. 로그인하라고 시켜야지..
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("로그인 하십시오.");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("access-token")) {
                accessToken = cookie.getValue();
            }
        }



// 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {

            filterChain.doFilter(request, response);

            return;
        }

// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

// 토큰이 access인지 확인 (발급시 페이로드에 명시) 이 부분도 뒤로 넘기면 안됨.
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


// username, role 값을 획득
        String memberLoginId = jwtUtil.getMemberLoginId(accessToken);
        String role = jwtUtil.getRole(accessToken);

        Member member = new Member();
        member.setMemberLoginId(memberLoginId);
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // "/login" 경로를 제외하여 JWT 필터가 동작하지 않도록 설정
        return path.equals("/api/auth/login");
    }
}
