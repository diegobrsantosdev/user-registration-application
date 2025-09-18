package com.diegobrsantosdev.user_registration_application.exceptions;

import java.io.Serial;

public class DatabaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public DatabaseException(String message) {
        super(message);
    }
}
