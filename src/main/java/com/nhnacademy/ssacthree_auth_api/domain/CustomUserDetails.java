package com.nhnacademy.ssacthree_auth_api.domain;


import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class CustomUserDetails implements UserDetails {

    private Member member;
    private Admin admin;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    public CustomUserDetails(Admin admin) {
        this.admin = admin;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        if (member != null) {
            collection.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return "ROLE_USER";
                }
            });
        } else {
            collection.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return "ROLE_ADMIN";
                }
            });
        }

        return collection;
    }

    @Override
    public String getPassword() {
        if (member != null) {
            return member.getMemberPassword();
        } else {
            return admin.getAdminPassword();
        }

    }

    @Override
    public String getUsername() {
        if (member != null) {
            return member.getMemberLoginId();
        } else {
            return admin.getAdminLoginId();
        }

    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

