package com.study.board3.exception;

public class Login_RestException extends RuntimeException{

    public Login_RestException() {
        super();
    }

    public Login_RestException(String message) {
        super(message);
    }

    public Login_RestException(String message, Throwable cause) {
        super(message, cause);
    }

    public Login_RestException(Throwable cause) {
        super(cause);
    }

    protected Login_RestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
