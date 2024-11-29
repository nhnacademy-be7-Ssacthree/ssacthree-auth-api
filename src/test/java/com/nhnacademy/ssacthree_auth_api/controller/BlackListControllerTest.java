package com.nhnacademy.ssacthree_auth_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nhnacademy.ssacthree_auth_api.service.BlackListService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BlackListController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class BlackListControllerTest {

    @MockBean
    private BlackListService blackListService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testValidateToken() throws Exception {
        // Arrange
        when(blackListService.isValidToken(any(HttpServletRequest.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/auth/validation"))
            .andExpect(status().isOk());

        // Arrange for invalid token
        when(blackListService.isValidToken(any(HttpServletRequest.class))).thenReturn(true);

        // Act & Assert for invalid token
        mockMvc.perform(post("/api/auth/validation"))
            .andExpect(status().isUnauthorized());
    }
}
