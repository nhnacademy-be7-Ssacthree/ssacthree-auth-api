package com.nhnacademy.ssacthree_auth_api.domain;


import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        if (member != null) {
            collection.add(() -> "ROLE_USER");
        }

        return collection;
    }

    @Override
    public String getPassword() {

        return member.getMemberPassword();

    }

    @Override
    public String getUsername() {

        return member.getMemberLoginId();


    }


}

