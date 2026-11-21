package com.cibertec.subscription;

public class ExpiredCardException extends RuntimeException {
    public ExpiredCardException(String msg) {
        super(msg);
    }
}