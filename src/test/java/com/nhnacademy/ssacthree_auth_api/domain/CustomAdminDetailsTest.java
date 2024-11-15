package com.nhnacademy.ssacthree_auth_api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class CustomAdminDetailsTest {

    private Admin admin;
    private CustomAdminDetails customAdminDetails;

    @BeforeEach
    void setUp() {
        admin = new Admin(1L, "testAdmin", "adminPassword", "testAdmin");
        customAdminDetails = new CustomAdminDetails(admin);
    }

    @Test
    @DisplayName("getAuthorities - 관리자 권한 확인")
    void getAuthorities() {
        // Act
        Collection<? extends GrantedAuthority> authorities = customAdminDetails.getAuthorities();

        // Assert
        assertNotNull(authorities, "Authorities 컬렉션이 null이면 안 됩니다.");
        assertEquals(1, authorities.size(), "권한은 하나여야 합니다.");
        assertEquals("ROLE_ADMIN", authorities.iterator().next().getAuthority(),
            "권한은 'ROLE_ADMIN'이어야 합니다.");
    }

    @Test
    @DisplayName("getPassword - 관리자 비밀번호 확인")
    void getPassword() {
        // Act
        String password = customAdminDetails.getPassword();

        // Assert
        assertEquals("adminPassword", password, "비밀번호는 'adminPassword'여야 합니다.");
    }

    @Test
    @DisplayName("getUsername - 관리자 이름 확인")
    void getUsername() {
        // Act
        String username = customAdminDetails.getUsername();

        // Assert
        assertEquals("testAdmin", username, "사용자 이름은 'testAdmin'이어야 합니다.");
    }

    @Test
    @DisplayName("isAccountNonExpired - 계정 만료 여부 확인")
    void isAccountNonExpired() {
        // Act & Assert
        assertTrue(customAdminDetails.isAccountNonExpired(), "계정은 만료되지 않아야 합니다.");
    }

    @Test
    @DisplayName("isAccountNonLocked - 계정 잠금 여부 확인")
    void isAccountNonLocked() {
        // Act & Assert
        assertTrue(customAdminDetails.isAccountNonLocked(), "계정은 잠금되지 않아야 합니다.");
    }

    @Test
    @DisplayName("isCredentialsNonExpired - 자격 증명 만료 여부 확인")
    void isCredentialsNonExpired() {
        // Act & Assert
        assertTrue(customAdminDetails.isCredentialsNonExpired(), "자격 증명은 만료되지 않아야 합니다.");
    }

    @Test
    @DisplayName("isEnabled - 계정 활성화 여부 확인")
    void isEnabled() {
        // Act & Assert
        assertTrue(customAdminDetails.isEnabled(), "계정은 활성화되어 있어야 합니다.");
    }
}
