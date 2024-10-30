package com.nhnacademy.ssacthree_auth_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Getter
@NoArgsConstructor
public class LoginRequestDto {

    private String memberLoginId;
    private String memberPassword;
}
