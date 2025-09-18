package com.diegobrsantosdev.user_registration_application.exceptions;

import java.io.Serial;

public class ResourceNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(Long id) {
        super("Resource not found. Id: " + id);


    }
}
