package com.nhnacademy.ssacthree_auth_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(("/api/auth/admin"))
public class AdminController {

    @PostMapping
    public ResponseEntity<?> checkAdmin() {
        return ResponseEntity.ok().build();
    }
}
