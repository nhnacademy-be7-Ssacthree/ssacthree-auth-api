package com.nhnacademy.ssacthree_auth_api.repository;

import com.nhnacademy.ssacthree_auth_api.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByAdminLoginId(String adminLoginId);
}
