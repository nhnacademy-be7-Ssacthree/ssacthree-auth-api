package com.nhnacademy.ssacthree_auth_api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ErrorResponse {

    private String message;
    private Integer status;

}
