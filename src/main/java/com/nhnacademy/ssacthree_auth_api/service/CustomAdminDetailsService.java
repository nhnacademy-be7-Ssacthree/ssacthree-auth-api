package com.nhnacademy.ssacthree_auth_api.service;

import com.nhnacademy.ssacthree_auth_api.domain.Admin;
import com.nhnacademy.ssacthree_auth_api.domain.CustomAdminDetails;
import com.nhnacademy.ssacthree_auth_api.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class CustomAdminDetailsService implements UserDetailsService {


    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String memberLoginId) throws UsernameNotFoundException {
        Admin adminData = adminRepository.findByAdminLoginId(memberLoginId);
        if (adminData != null) {
            return new CustomAdminDetails(adminData);
        }
        return null;
    }
}
