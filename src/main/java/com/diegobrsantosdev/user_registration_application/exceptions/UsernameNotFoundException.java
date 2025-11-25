package com.diegobrsantosdev.user_registration_application.exceptions;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String email) {
      super("User not found with email: " + email);
    }
}
