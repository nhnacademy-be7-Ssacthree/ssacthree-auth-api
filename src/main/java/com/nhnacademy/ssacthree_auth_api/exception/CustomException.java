package com.nhnacademy.ssacthree_auth_api.exception;

import lombok.Getter;

public class CustomException extends RuntimeException {

    @Getter
    private Integer statusCode;

    public CustomException(String message, Integer status) {
        super(message);
        this.statusCode = status;
    }
}
