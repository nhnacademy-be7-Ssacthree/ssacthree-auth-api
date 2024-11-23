package com.nhnacademy.ssacthree_auth_api.controller;

import com.nhnacademy.ssacthree_auth_api.dto.PaycoLoginRequest;
import com.nhnacademy.ssacthree_auth_api.service.PaycoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/payco-login")
@RequiredArgsConstructor
public class PaycoLoginController {

    private final PaycoService paycoService;

    @PostMapping
    public ResponseEntity<String> paycoLogin(
        @RequestBody PaycoLoginRequest paycoLoginRequest,
        HttpServletResponse response) {
        paycoService.PaycoLogin(paycoLoginRequest.getPaycoIdNo(), response);
        return ResponseEntity.ok("로그인 성공");
    }
}
