package com.diegobrsantosdev.user_registration_application.viaCep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CepService {

    @Autowired
    private  RestTemplate restTemplate;

    @Cacheable(value = "ceps", key = "#cep")
    public CepResponseDTO lookupCep(String cep) {
        validateCep(cep);

        // Validating format before calling the API

        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        try {
            ViaCepResponse viaCep = restTemplate.getForObject(url, ViaCepResponse.class);

            if (viaCep == null || viaCep.zipCode() == null) {
                throw new CepNotFoundException(cep);
            }
            // cep not found

            return new CepResponseDTO(
                    viaCep.zipCode(),
                    viaCep.address(),
                    viaCep.complement(),
                    viaCep.neighborhood(),
                    viaCep.city(),
                    viaCep.state()
            );
            // Cep ok: build and return the response DTO

        }  catch (ResourceAccessException e) {
                throw new RuntimeException("ViaCep API is not responding. Please try again later.");

                // Timeout or connection error

        } catch (RestClientException e) {
            throw new RuntimeException("Error while calling ViaCep API: " + e.getMessage());

            // another error related to cep
        }

    }

    private void validateCep(String cep) {
        if (cep == null || !cep.matches("^[0-9]{8}$")) {
            throw new IllegalArgumentException("Invalid CEP format. Use only 8 numeric digits.");
        }
    }
    // method to validate CEP format
}