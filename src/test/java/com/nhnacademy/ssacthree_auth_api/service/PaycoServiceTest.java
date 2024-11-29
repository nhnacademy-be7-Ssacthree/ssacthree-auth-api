package com.nhnacademy.ssacthree_auth_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nhnacademy.ssacthree_auth_api.domain.Member;
import com.nhnacademy.ssacthree_auth_api.domain.RefreshToken;
import com.nhnacademy.ssacthree_auth_api.exception.MemberNotFoundException;
import com.nhnacademy.ssacthree_auth_api.exception.PaycoAlreadyConnectException;
import com.nhnacademy.ssacthree_auth_api.exception.SleepMemberException;
import com.nhnacademy.ssacthree_auth_api.jwt.JWTUtil;
import com.nhnacademy.ssacthree_auth_api.repository.MemberRepository;
import com.nhnacademy.ssacthree_auth_api.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaycoServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private PaycoService paycoService;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        // @InjectMocks를 사용하여 자동으로 주입됩니다.
    }

    @Test
    void testPaycoLogin_Success() {
        // Given
        String paycoIdNo = "payco123";
        String memberLoginId = "member123";
        String role = "ROLE_USER";

        Member member = new Member();
        member.setPaycoIdNumber(paycoIdNo);
        member.setMemberLoginId(memberLoginId);
        member.setMemberStatus("active");

        when(memberRepository.findByPaycoIdNumber(paycoIdNo)).thenReturn(Optional.of(member));

        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(jwtUtil.createJwt(eq("access"), eq(memberLoginId), eq(role), anyLong())).thenReturn(
            accessToken);
        when(jwtUtil.createJwt(eq("refresh"), eq(memberLoginId), eq(role), anyLong())).thenReturn(
            refreshToken);

        doNothing().when(response).addCookie(any(Cookie.class));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // When
        paycoService.paycoLogin(paycoIdNo, response);

        // Then
        verify(response, times(2)).addCookie(any(Cookie.class));
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberCaptor.capture());
        Member savedMember = memberCaptor.getValue();

        assertNotNull(savedMember.getMemberLastLoginAt());
    }

    @Test
    void testPaycoLogin_MemberNotFound() {
        // Given
        String paycoIdNo = "payco123";
        when(memberRepository.findByPaycoIdNumber(paycoIdNo)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(MemberNotFoundException.class,
            () -> paycoService.paycoLogin(paycoIdNo, response));
    }

    @Test
    void testPaycoLogin_SleepMember() {
        // Given
        String paycoIdNo = "payco123";

        Member member = new Member();
        member.setPaycoIdNumber(paycoIdNo);
        member.setMemberStatus("sleep");

        when(memberRepository.findByPaycoIdNumber(paycoIdNo)).thenReturn(Optional.of(member));

        // When & Then
        assertThrows(SleepMemberException.class,
            () -> paycoService.paycoLogin(paycoIdNo, response));
    }

    @Test
    void testPaycoConnection_Success() {
        // Given
        String paycoIdNo = "payco123";
        String memberLoginId = "member123";

        Member member = new Member();
        member.setMemberLoginId(memberLoginId);

        when(memberRepository.findByMemberLoginId(memberLoginId)).thenReturn(member);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // When
        paycoService.paycoConnection(paycoIdNo, memberLoginId);

        // Then
        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberCaptor.capture());
        Member savedMember = memberCaptor.getValue();

        assertEquals(paycoIdNo, savedMember.getPaycoIdNumber());
    }

    @Test
    void testPaycoConnection_AlreadyConnected() {
        // Given
        String paycoIdNo = "payco123";
        String memberLoginId = "member123";

        Member member = new Member();
        member.setMemberLoginId(memberLoginId);
        member.setPaycoIdNumber("existingPaycoId");

        when(memberRepository.findByMemberLoginId(memberLoginId)).thenReturn(member);

        // When & Then
        assertThrows(PaycoAlreadyConnectException.class,
            () -> paycoService.paycoConnection(paycoIdNo, memberLoginId));
    }
}
