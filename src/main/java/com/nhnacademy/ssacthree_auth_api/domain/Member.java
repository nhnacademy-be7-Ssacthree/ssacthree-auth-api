package com.nhnacademy.ssacthree_auth_api.domain;

import jakarta.persistence.Entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "member")
public class Member {

    @Id
    @Column(name = "customer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;


    @Column(name= "member_grade_id")
    private Long memberGradeId;

    @Column(name= "member_login_id")
    @Setter
    private String memberLoginId;

    @Column(name= "member_password")
    private String memberPassword;

    @Column(name= "member_birthdate")
    private String memberBirthdate;

    @Column(name= "member_created_at")
    private LocalDateTime memberCreatedAt;

    @Column(name= "member_last_login_at")
    private LocalDateTime memberLastLoginAt;

    @Column(name= "member_status")
    private String memberStatus;

    @Column(name= "member_point")
    private int memberPoint;


}

