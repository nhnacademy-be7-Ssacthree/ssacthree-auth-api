package com.nhnacademy.ssacthree_auth_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ssacthree_auth_api.dto.PaycoLoginRequest;
import com.nhnacademy.ssacthree_auth_api.exception.MemberNotFoundException;
import com.nhnacademy.ssacthree_auth_api.exception.PaycoAlreadyConnectException;
import com.nhnacademy.ssacthree_auth_api.service.PaycoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaycoLoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaycoLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaycoService paycoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Payco 로그인 성공 테스트")
    void paycoLoginSuccess() throws Exception {
        // Given
        PaycoLoginRequest request = new PaycoLoginRequest("payco123");

        // When
        Mockito.doNothing().when(paycoService).paycoLogin(eq("payco123"), any());

        // Then
        mockMvc.perform(post("/api/auth/payco-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string("로그인 성공"));
    }

    @Test
    @DisplayName("Payco 계정 연동 성공 테스트")
    void paycoConnectionSuccess() throws Exception {
        // Given
        PaycoLoginRequest request = new PaycoLoginRequest("payco456");
        String memberLoginId = "user123";

        // When
        Mockito.doNothing().when(paycoService).paycoConnection("payco456", "user123");

        // Then
        mockMvc.perform(post("/api/auth/payco-connection")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", memberLoginId)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().string("연동 성공"));
    }

    @Test
    @DisplayName("Payco 로그인 실패 - 없는 계정")
    void paycoLoginFail_MemberNotFound() throws Exception {
        // Given
        PaycoLoginRequest request = new PaycoLoginRequest("nonexistentPaycoId");

        // When
        Mockito.doThrow(new MemberNotFoundException("연동된 계정을 찾을 수 없습니다."))
            .when(paycoService).paycoLogin(eq("nonexistentPaycoId"), any());

        // Then
        mockMvc.perform(post("/api/auth/payco-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("연동된 계정을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("Payco 연동 실패 - 이미 연동된 계정")
    void paycoConnectionFail_AlreadyConnected() throws Exception {
        // Given
        PaycoLoginRequest request = new PaycoLoginRequest("alreadyConnectedPaycoId");
        String memberLoginId = "user123";

        // When
        Mockito.doThrow(new PaycoAlreadyConnectException("이미 연동 된 계정입니다!"))
            .when(paycoService).paycoConnection("alreadyConnectedPaycoId", "user123");

        // Then
        mockMvc.perform(post("/api/auth/payco-connection")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-USER-ID", memberLoginId)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("이미 연동 된 계정입니다!"));
    }
}
