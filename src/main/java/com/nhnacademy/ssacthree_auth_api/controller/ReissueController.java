package com.nhnacademy.ssacthree_auth_api.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final long accessTokenExpired = 600000L;
    private final long refreshTokenExpired = 3600000L;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        //get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh-token")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // 어느정도 검증 후 DB에 저장되어있는지 확인해야함.
        boolean isExist = refreshTokenRepository.existsByRefreshToken(refresh);
        if(!isExist) {
            return new ResponseEntity<>("refresh token does not exist", HttpStatus.BAD_REQUEST);
        }

        String memberLoginId = jwtUtil.getMemberLoginId(refresh);
        String role = jwtUtil.getRole(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("access", memberLoginId, role, accessTokenExpired);
        String newRefresh = jwtUtil.createJwt("refresh", memberLoginId, role, refreshTokenExpired);

        refreshTokenRepository.deleteById(jwtUtil.getMemberLoginId(refresh));
        addRefreshToken(memberLoginId, newRefresh, refreshTokenExpired);
        //response
        response.addCookie(createCookie("access-token",newAccess,accessTokenExpired));
        response.addCookie(createCookie("refresh-token",newRefresh,refreshTokenExpired));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void addRefreshToken(String memberLoginId, String refresh, long expiredMs) {

        RefreshToken refreshToken = new RefreshToken(memberLoginId,refresh,expiredMs);
        refreshTokenRepository.save(refreshToken);
    }

    private Cookie createCookie(String key, String value, long expiredMs) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge((int)expiredMs);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }


}
