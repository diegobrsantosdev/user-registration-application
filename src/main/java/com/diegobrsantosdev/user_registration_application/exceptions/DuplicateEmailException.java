package com.diegobrsantosdev.user_registration_application.exceptions;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {

      super(message);
    }
}
