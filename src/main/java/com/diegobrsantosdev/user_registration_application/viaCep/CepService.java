package com.diegobrsantosdev.user_registration_application.viaCep;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;



@Service
public class CepService {

    private final RestTemplate restTemplate;
    private final String baseUrl;


    public CepService(RestTemplate restTemplate, @Value("${viacep.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }


    @Cacheable(value = "ceps", key = "#cep")
    public CepResponseDTO lookupCep(String cep) {
        validateCep(cep);
        String url = baseUrl + cep + "/json/";

        try {
            ViaCepResponse viaCep = restTemplate.getForObject(url, ViaCepResponse.class);

            if (viaCep == null || viaCep.zipCode() == null) {
                throw new CepNotFoundException(cep);
            }

            return new CepResponseDTO(
                    viaCep.zipCode(),
                    viaCep.address(),
                    viaCep.complement(),
                    viaCep.neighborhood(),
                    viaCep.city(),
                    viaCep.state()
            );

        } catch (ResourceAccessException e) {
            throw new RuntimeException("ViaCep API is not responding. Please try again later.");
        } catch (RestClientException e) {
            throw new RuntimeException("Error while calling ViaCep API: " + e.getMessage());
        }
    }

    private void validateCep(String cep) {
        if (cep == null || !cep.matches("^[0-9]{8}$")) {
            throw new IllegalArgumentException("Invalid CEP format. Use only 8 numeric digits.");
        }
    }
}