package com.nhnacademy.ssacthree_auth_api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nhnacademy.ssacthree_auth_api.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReissueController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class ReissueControllerTest {

    @MockBean
    private ReissueService reissueService;


    @Autowired
    private MockMvc mockMvc;

    @Test
    void testReissue() throws Exception {
        when(reissueService.reissueRefreshToken(any(HttpServletRequest.class), any(
            HttpServletResponse.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/api/auth/reissue"))
            .andExpect(status().isOk());
    }


}
