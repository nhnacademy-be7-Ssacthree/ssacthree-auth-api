package com.nhnacademy.ssacthree_auth_api.exception;

import lombok.Getter;

public class CustomException extends RuntimeException {

    @Getter
    private Integer status;

    public CustomException(String message, Integer status) {
        super(message);
        this.status = status;
    }
}
