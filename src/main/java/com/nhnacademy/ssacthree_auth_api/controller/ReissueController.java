package com.nhnacademy.ssacthree_auth_api.controller;

import com.nhnacademy.ssacthree_auth_api.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ReissueController {

    private final ReissueService reissueService;

    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request,
        HttpServletResponse response) {
        return reissueService.reissueRefreshToken(request, response);
    }


}
