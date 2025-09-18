package com.diegobrsantosdev.user_registration_application.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCpfValidator implements ConstraintValidator<ValidCpf, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || !cpf.matches("\\d{11}")) return false;
        // Ignore blocked CPFs
        if (cpf.chars().distinct().count() == 1) return false;

        //Calculation of check digits

        try {
            int soma = 0, peso = 10;
            for (int i = 0; i < 9; i++)
                soma += (cpf.charAt(i) - '0') * peso--;

            int dig1 = 11 - (soma % 11);
            if (dig1 >= 10) dig1 = 0;

            soma = 0; peso = 11;
            for (int i = 0; i < 10; i++)
                soma += (cpf.charAt(i) - '0') * peso--;

            int dig2 = 11 - (soma % 11);
            if (dig2 >= 10) dig2 = 0;

            return cpf.charAt(9) - '0' == dig1 && cpf.charAt(10) - '0' == dig2;
        } catch (Exception e) {
            return false;
        }
    }
}