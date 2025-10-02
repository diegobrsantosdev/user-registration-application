package com.diegobrsantosdev.user_registration_application.exceptions;

public class DuplicateCpfException extends RuntimeException {

    public DuplicateCpfException(String message) {

        super(message);
    }
}
