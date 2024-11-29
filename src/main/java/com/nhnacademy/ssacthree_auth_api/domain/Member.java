package com.nhnacademy.ssacthree_auth_api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "member")
public class Member implements Serializable {

    @Id
    @Column(name = "customer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;


    @Column(name = "member_grade_id")
    private Long memberGradeId;

    @Column(name = "member_login_id")
    @Setter
    private String memberLoginId;

    @Column(name = "member_password")
    private String memberPassword;

    @Column(name = "member_birthdate")
    private String memberBirthdate;

    @Column(name = "member_created_at")
    private LocalDateTime memberCreatedAt;

    @Setter
    @Column(name = "member_last_login_at")
    private LocalDateTime memberLastLoginAt;

    @Setter
    @Column(name = "member_status")
    private String memberStatus;

    @Column(name = "member_point")
    private int memberPoint;

    @Setter
    @Column(name = "payco_id_number")
    private String paycoIdNumber;

}

