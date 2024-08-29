package com.pranshu.EcomUserService.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}
