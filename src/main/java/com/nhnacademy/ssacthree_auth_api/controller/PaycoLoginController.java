package com.nhnacademy.ssacthree_auth_api.controller;

import com.nhnacademy.ssacthree_auth_api.dto.PaycoLoginRequest;
import com.nhnacademy.ssacthree_auth_api.service.PaycoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaycoLoginController {

    private final PaycoService paycoService;

    @PostMapping(("/api/auth/payco-login"))
    public ResponseEntity<String> paycoLogin(
        @RequestBody PaycoLoginRequest paycoLoginRequest,
        HttpServletResponse response) {
        paycoService.paycoLogin(paycoLoginRequest.getPaycoIdNo(), response);
        return ResponseEntity.ok("로그인 성공");
    }

    @PostMapping("/api/auth/payco-connection")
    public ResponseEntity<String> paycoConnection(@RequestBody PaycoLoginRequest paycoLoginRequest,
        @RequestHeader(name = "X-USER-ID") String memberLoginId) {
        paycoService.paycoConnection(paycoLoginRequest.getPaycoIdNo(), memberLoginId);
        return ResponseEntity.ok("연동 성공");
    }
}
