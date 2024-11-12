package com.nhnacademy.ssacthree_auth_api.service;

import com.nhnacademy.ssacthree_auth_api.domain.Admin;
import com.nhnacademy.ssacthree_auth_api.domain.CustomUserDetails;
import com.nhnacademy.ssacthree_auth_api.domain.Member;
import com.nhnacademy.ssacthree_auth_api.exception.WithdrawMemberException;
import com.nhnacademy.ssacthree_auth_api.repository.AdminRepository;
import com.nhnacademy.ssacthree_auth_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String memberLoginId) throws UsernameNotFoundException {

        Member memberData = memberRepository.findByMemberLoginId(memberLoginId);
        Admin adminData = adminRepository.findByAdminLoginId(memberLoginId);
        if (memberData != null) {
            if (memberData.getMemberStatus().equalsIgnoreCase("withdraw")) {
                throw new WithdrawMemberException("탈퇴한 회원입니다.");
            }
            return new CustomUserDetails(memberData);
        }

        if (adminData != null) {
            return new CustomUserDetails(adminData);
        }
        return null;

    }
}
