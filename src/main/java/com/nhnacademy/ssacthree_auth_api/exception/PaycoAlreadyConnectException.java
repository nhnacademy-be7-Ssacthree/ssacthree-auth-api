package com.nhnacademy.ssacthree_auth_api.exception;

public class PaycoAlreadyConnectException extends CustomException {

    public PaycoAlreadyConnectException(String message) {
        super(message, 400);
    }
}
