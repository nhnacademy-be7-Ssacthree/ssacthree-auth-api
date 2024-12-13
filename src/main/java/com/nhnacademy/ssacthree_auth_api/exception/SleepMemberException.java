package com.nhnacademy.ssacthree_auth_api.exception;

public class SleepMemberException extends CustomException {

    public SleepMemberException(String message) {
        super(message, 400);
    }
}
