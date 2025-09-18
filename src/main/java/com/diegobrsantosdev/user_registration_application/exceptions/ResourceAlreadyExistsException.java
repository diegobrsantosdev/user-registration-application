package com.diegobrsantosdev.user_registration_application.exceptions;

import java.io.Serial;

public class ResourceAlreadyExistsException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
