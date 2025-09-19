package com.diegobrsantosdev.user_registration_application.viaCep;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cep")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/{cep}")
    public CepResponseDTO getAddressByCep(@PathVariable String cep) {
        return cepService.lookupCep(cep);
    }
}