package com.nhnacademy.ssacthree_auth_api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.ssacthree_auth_api.jwt.AdminLoginFilter;
import com.nhnacademy.ssacthree_auth_api.jwt.CustomLogoutFilter;
import com.nhnacademy.ssacthree_auth_api.jwt.JWTUtil;
import com.nhnacademy.ssacthree_auth_api.jwt.LoginFilter;
import com.nhnacademy.ssacthree_auth_api.repository.AdminRepository;
import com.nhnacademy.ssacthree_auth_api.repository.MemberRepository;
import com.nhnacademy.ssacthree_auth_api.repository.RefreshTokenRepository;
import com.nhnacademy.ssacthree_auth_api.service.BlackListService;
import com.nhnacademy.ssacthree_auth_api.service.CustomAdminDetailsService;
import com.nhnacademy.ssacthree_auth_api.service.CustomUserDetailsService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final BlackListService blackListService;
    private final AdminRepository adminRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AdminRepository adminRepository)
        throws Exception {

        http.formLogin(AbstractHttpConfigurer::disable);

        http.httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((auth) -> auth
            .requestMatchers("/api/auth/login", "/", "/register", "/api/auth/admin-login")
            .permitAll()
            .requestMatchers("/api/auth/reissue").permitAll()
            .requestMatchers("/api/auth/validation").permitAll()
            .requestMatchers("/api/auth/admin").permitAll()
            .requestMatchers("/api/auth/payco-login").permitAll()
            .requestMatchers("/api/auth/payco-connection").permitAll()
            .anyRequest().authenticated());

        LoginFilter loginFilter = new LoginFilter(
            userAuthenticationManager(), jwtUtil, objectMapper,
            refreshTokenRepository, memberRepository);

        AdminLoginFilter adminLoginFilter = new AdminLoginFilter(
            adminAuthenticationManager(), jwtUtil, objectMapper,
            refreshTokenRepository
        );

        loginFilter.setFilterProcessesUrl("/api/auth/login");
        adminLoginFilter.setFilterProcessesUrl("/api/auth/admin-login");

        http.addFilterBefore(adminLoginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(
            new CustomLogoutFilter(jwtUtil, refreshTokenRepository, blackListService),
            LogoutFilter.class);

        http.sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public DaoAuthenticationProvider authenticationUserProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(new CustomUserDetailsService(memberRepository));
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(new CustomAdminDetailsService(adminRepository));
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    @Primary
    public AuthenticationManager userAuthenticationManager() {
        return new ProviderManager(Collections.singletonList(authenticationUserProvider()));
    }

    @Bean
    public AuthenticationManager adminAuthenticationManager() {
        return new ProviderManager(Collections.singletonList(adminAuthenticationProvider()));
    }
}
