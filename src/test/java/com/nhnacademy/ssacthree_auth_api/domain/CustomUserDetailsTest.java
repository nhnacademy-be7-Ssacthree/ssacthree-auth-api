package com.nhnacademy.ssacthree_auth_api.domain;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class CustomUserDetailsTest {

    private Member member;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        member = new Member(1L, 1L, "testUser", "testPassword", "20000101",
            LocalDateTime.now(),
            null, "ACTIVE", 0, null);
        customUserDetails = new CustomUserDetails(member);
    }

    @Test
    @DisplayName("getAuthorities - 사용자 권한 확인")
    void getAuthorities() {
        // Act
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

        // Assert
        assertNotNull(authorities, "Authorities 컬렉션이 null이면 안 됩니다.");
        assertEquals(1, authorities.size(), "권한은 하나여야 합니다.");
        assertEquals("ROLE_USER", authorities.iterator().next().getAuthority(),
            "권한은 'ROLE_USER'여야 합니다.");
    }

    @Test
    @DisplayName("getPassword - 사용자 비밀번호 확인")
    void getPassword() {
        // Act
        String password = customUserDetails.getPassword();

        // Assert
        assertEquals("testPassword", password, "비밀번호는 'testPassword'여야 합니다.");
    }

    @Test
    @DisplayName("getUsername - 사용자 이름 확인")
    void getUsername() {
        // Act
        String username = customUserDetails.getUsername();

        // Assert
        assertEquals("testUser", username, "사용자 이름은 'testUser'여야 합니다.");
    }

    @Test
    @DisplayName("isAccountNonExpired - 계정 만료 여부 확인")
    void isAccountNonExpired() {
        // Act & Assert
        assertTrue(customUserDetails.isAccountNonExpired(), "계정은 만료되지 않아야 합니다.");
    }

    @Test
    @DisplayName("isAccountNonLocked - 계정 잠금 여부 확인")
    void isAccountNonLocked() {
        // Act & Assert
        assertTrue(customUserDetails.isAccountNonLocked(), "계정은 잠금되지 않아야 합니다.");
    }

    @Test
    @DisplayName("isCredentialsNonExpired - 자격 증명 만료 여부 확인")
    void isCredentialsNonExpired() {
        // Act & Assert
        assertTrue(customUserDetails.isCredentialsNonExpired(), "자격 증명은 만료되지 않아야 합니다.");
    }

    @Test
    @DisplayName("isEnabled - 계정 활성화 여부 확인")
    void isEnabled() {
        // Act & Assert
        assertTrue(customUserDetails.isEnabled(), "계정은 활성화되어 있어야 합니다.");
    }
}

