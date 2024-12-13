package com.nhnacademy.ssacthree_auth_api.service;

import com.nhnacademy.ssacthree_auth_api.domain.RefreshToken;
import com.nhnacademy.ssacthree_auth_api.jwt.JWTUtil;
import com.nhnacademy.ssacthree_auth_api.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final long ACCESS_TOKEN_EXPIRED = 30 * 60 * 1000L; // 30분
    private static final long REFRESH_TOKEN_EXPIRED = 120 * 60 * 1000L; // 2시간

    public ResponseEntity<String> reissueRefreshToken(HttpServletRequest request,
        HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh-token")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // 만료시간 체크
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // 어느정도 검증 후 DB에 저장되어있는지 확인해야함.
        boolean isExist = refreshTokenRepository.existsByRefreshToken(refresh);
        if (!isExist) {
            return new ResponseEntity<>("refresh token does not exist", HttpStatus.BAD_REQUEST);
        }

        String memberLoginId = jwtUtil.getMemberLoginId(refresh);
        String role = jwtUtil.getRole(refresh);

        // 새 jwt 생성함.
        String newAccess = jwtUtil.createJwt("access", memberLoginId, role, ACCESS_TOKEN_EXPIRED);
        String newRefresh = jwtUtil.createJwt("refresh", memberLoginId, role,
            REFRESH_TOKEN_EXPIRED);

        refreshTokenRepository.deleteById(jwtUtil.getMemberLoginId(refresh));

        addRefreshToken(memberLoginId, newRefresh, REFRESH_TOKEN_EXPIRED);
        //response
        response.addCookie(createCookie("access-token", newAccess, ACCESS_TOKEN_EXPIRED));
        response.addCookie(createCookie("refresh-token", newRefresh, REFRESH_TOKEN_EXPIRED));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void addRefreshToken(String memberLoginId, String refresh, long expiredMs) {

        RefreshToken refreshToken = new RefreshToken(memberLoginId, refresh, expiredMs);
        refreshTokenRepository.save(refreshToken);
    }

    private Cookie createCookie(String key, String value, long expiredMs) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge((int) expiredMs / 1000);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
