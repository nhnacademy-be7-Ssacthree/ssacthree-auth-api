package com.nhnacademy.ssacthree_auth_api.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private Integer status;

}
