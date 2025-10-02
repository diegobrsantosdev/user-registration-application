package com.diegobrsantosdev.user_registration_application.exceptions;

import com.diegobrsantosdev.user_registration_application.viaCep.CepNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        String error = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(
                Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<StandardError> resourceAlreadyExists(ResourceAlreadyExistsException e, HttpServletRequest request) {
        String error = "Resource already exists";
        HttpStatus status = HttpStatus.CONFLICT; // 409
        StandardError err = new StandardError(
                Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> illegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        String error = "Invalid argument";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(
                Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String error = "Validation error";
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        StringBuilder sb = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(
                err -> sb.append(err.getField())
                        .append(": ")
                        .append(err.getDefaultMessage())
                        .append("; "));

        StandardError err = new StandardError(
                Instant.now(), status.value(), error, sb.toString(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> generic(Exception e, HttpServletRequest request) {
        String error = "Unexpected error";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        StandardError err = new StandardError(
                Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public ResponseEntity<StandardError> incorrectPassword(IncorrectPasswordException e, HttpServletRequest request) {
        String error = "Incorrect password";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(
                Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> handleDatabaseIntegrity(Exception ex, HttpServletRequest request) {
        StandardError err = new StandardError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Database error",
                "Data integrity error" + ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(CepNotFoundException.class)
    public ResponseEntity<StandardError> cepNotFound(CepNotFoundException e, HttpServletRequest request) {
        String error = "CEP not found";
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(
                Instant.now(), status.value(), error, e.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DuplicateCpfException.class)
    public ResponseEntity<String> handleDuplicateCpf(DuplicateCpfException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}


