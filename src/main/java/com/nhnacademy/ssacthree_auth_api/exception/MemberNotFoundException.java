package com.nhnacademy.ssacthree_auth_api.exception;

public class MemberNotFoundException extends CustomException {


    public MemberNotFoundException(String message) {
        super(message, 404);
    }
}
