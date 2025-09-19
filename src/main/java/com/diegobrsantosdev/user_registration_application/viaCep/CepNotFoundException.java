package com.diegobrsantosdev.user_registration_application.viaCep;

public class CepNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public CepNotFoundException(String cep) {
        super("CEP not found: " + cep);
    }
}
