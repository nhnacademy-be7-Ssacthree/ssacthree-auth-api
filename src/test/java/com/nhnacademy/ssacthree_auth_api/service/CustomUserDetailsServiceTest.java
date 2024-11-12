package com.nhnacademy.ssacthree_auth_api.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.nhnacademy.ssacthree_auth_api.domain.CustomUserDetails;
import com.nhnacademy.ssacthree_auth_api.domain.Member;
import com.nhnacademy.ssacthree_auth_api.exception.WithdrawMemberException;
import com.nhnacademy.ssacthree_auth_api.repository.AdminRepository;
import com.nhnacademy.ssacthree_auth_api.repository.MemberRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AdminRepository adminRepository;


    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testLoadUserByUsername() {

        String memberLoginId = "test";
        Member member = new Member(1L, 1L, memberLoginId, "test", "20000101", LocalDateTime.now(),
            null, "ACTIVE", 0);

        when(memberRepository.findByMemberLoginId(memberLoginId)).thenReturn(member);
        when(adminRepository.findByAdminLoginId(memberLoginId)).thenReturn(null);
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(
            memberLoginId);

        assertNotNull(userDetails);
    }

    @Test
    void testLoadUserByUsernameIfWithdraw() {
        String memberLoginId = "test";

        Member member = new Member(1L, 1L, memberLoginId, "test", "20000101", LocalDateTime.now(),
            null, "WITHDRAW", 0);

        when(memberRepository.findByMemberLoginId(memberLoginId)).thenReturn(member);
        when(adminRepository.findByAdminLoginId(memberLoginId)).thenReturn(null);
        assertThrows(WithdrawMemberException.class,
            () -> customUserDetailsService.loadUserByUsername(memberLoginId));
    }

    @Test
    void testLoadUserByUsernameIfNull() {

        when(memberRepository.findByMemberLoginId("test")).thenReturn(null);
        when(adminRepository.findByAdminLoginId("test")).thenReturn(null);
        assertNull(customUserDetailsService.loadUserByUsername("test"));
    }
}
