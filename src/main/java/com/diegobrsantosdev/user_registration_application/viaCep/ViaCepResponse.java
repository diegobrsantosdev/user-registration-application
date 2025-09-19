package com.diegobrsantosdev.user_registration_application.viaCep;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ViaCepResponse(
    @JsonProperty("cep")
    String zipCode,

    @JsonProperty("logradouro")
    String address,

    @JsonProperty("complemento")
    String complement,

    @JsonProperty("bairro")
    String neighborhood,

    @JsonProperty("localidade")
    String city,

    @JsonProperty("uf")
    String state

) {}