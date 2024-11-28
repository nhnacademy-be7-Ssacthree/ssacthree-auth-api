package com.nhnacademy.ssacthree_auth_api.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.nhnacademy.ssacthree_auth_api.domain.Admin;
import com.nhnacademy.ssacthree_auth_api.domain.CustomAdminDetails;
import com.nhnacademy.ssacthree_auth_api.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomAdminDetailsServiceTest {

    @Mock
    private AdminRepository adminRepository;


    @InjectMocks
    private CustomAdminDetailsService customAdminDetailsService;

    @Test
    void testLoadAdminByAdminId() {

        String adminLoginId = "test";
        Admin admin = new Admin(1L, adminLoginId, "test", "test");

        when(adminRepository.findByAdminLoginId(adminLoginId)).thenReturn(admin);
        CustomAdminDetails userDetails = (CustomAdminDetails) customAdminDetailsService.loadUserByUsername(
            adminLoginId);

        assertNotNull(userDetails);
    }

    @Test
    void testLoadAdminByUsername() {

        String adminLoginId = "test";
        Admin admin = new Admin(1L, adminLoginId, "password", "test");

        when(adminRepository.findByAdminLoginId(adminLoginId)).thenReturn(admin);
        CustomAdminDetails adminDetails = (CustomAdminDetails) customAdminDetailsService.loadUserByUsername(
            adminLoginId);
    }


    @Test
    void testLoadUserByUsernameIfNull() {

        when(adminRepository.findByAdminLoginId("test")).thenReturn(null);
        assertNull(customAdminDetailsService.loadUserByUsername("test"));
    }

}
