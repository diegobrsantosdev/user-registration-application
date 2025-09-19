package com.diegobrsantosdev.user_registration_application.viaCep;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CepService {

    public CepResponseDTO lookupCep(String cep) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://viacep.com.br/ws/" + cep + "/json/";
        ViaCepResponse viaCep = restTemplate.getForObject(url, ViaCepResponse.class);

        if (viaCep == null || viaCep.zipCode() == null){
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
    }
}