package com.diegobrsantosdev.user_registration_application.exceptions;

public class IncorrectPasswordException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IncorrectPasswordException(String msg) {
        super(msg);
    }

}
