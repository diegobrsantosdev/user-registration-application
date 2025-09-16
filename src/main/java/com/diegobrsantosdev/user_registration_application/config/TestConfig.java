package com.diegobrsantosdev.user_registration_application.config;

import com.diegobrsantosdev.user_registration_application.infrastructure.entities.User;
import com.diegobrsantosdev.user_registration_application.infrastructure.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("Test")
public class TestConfig {

    @Bean
    public CommandLineRunner loader(UserRepository userRepository) {
        return args -> {
            userRepository.save(User.builder()
                .name("João Silva")
                .email("joao@email.com")
                .password("senha1234")
                .cpf("12345678901")
                .phone("11999998888")
                .adress("Rua Alpha")
                .number("100")
                .complement("Apto 12")
                .neighborhood("Centro")
                .city("São Paulo")
                .state("SP")
                .zipCode("01001000")
                .build()
            );

            userRepository.save(User.builder()
                .name("Maria Souza")
                .email("maria@email.com")
                .password("senha5678")
                .cpf("98765432100")
                .phone("21988887777")
                .adress("Rua Beta")
                .number("500")
                .complement("")
                .neighborhood("Bela Vista")
                .city("Rio de Janeiro")
                .state("RJ")
                .zipCode("20020060")
                .build()
            );

            userRepository.save(User.builder()
                    .name("Caio Pereira")
                    .email("caiopereiraaa19@gmail.com")
                    .password("56edabc10")
                    .cpf("48520695490")
                    .phone("81987501006")
                    .adress("Rua Padre Alencar")
                    .number("256")
                    .complement("casa")
                    .neighborhood("Santo Amaro")
                    .city("Recife")
                    .state("PE")
                    .zipCode("50070000")
                    .build()
            );

        };
    }
}