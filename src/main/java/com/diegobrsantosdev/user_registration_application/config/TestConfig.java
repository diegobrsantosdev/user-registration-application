package com.diegobrsantosdev.user_registration_application.config;

import com.diegobrsantosdev.user_registration_application.models.Gender;
import com.diegobrsantosdev.user_registration_application.models.User;
import com.diegobrsantosdev.user_registration_application.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;


@Configuration
@Profile("test")
@ComponentScan(basePackages = {"com.diegobrsantosdev.user_registration_application"})
@Import(SecurityConfig.class)
public class TestConfig {

    @Bean
    public CommandLineRunner loader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            userRepository.save(User.builder()
                    .name("João Silva")
                    .email("joao@email.com")
                    .password(passwordEncoder.encode("senha1234"))
                    .cpf("12345678901")
                    .rg("12345678")
                    .phone("11999998888")
                    .address("Rua Alpha")
                    .number("100")
                    .complement("Apto 12")
                    .neighborhood("Centro")
                    .city("São Paulo")
                    .state("SP")
                    .zipCode("01001000")
                    .gender(Gender.MALE)
                    .dateOfBirth(LocalDate.of(1990, 5, 15))
                    .termsAccepted(true)
                    .profilePictureUrl(null)
                    .build()
            );

            userRepository.save(User.builder()
                    .name("Maria Souza")
                    .email("maria@email.com")
                    .password(passwordEncoder.encode("senha5678"))
                    .cpf("98765432100")
                    .rg("87654321")
                    .phone("21988887777")
                    .address("Rua Beta")
                    .number("500")
                    .complement("Casa")
                    .neighborhood("Bela Vista")
                    .city("Rio de Janeiro")
                    .state("RJ")
                    .zipCode("20020060")
                    .gender(Gender.FEMALE)
                    .dateOfBirth(LocalDate.of(1992, 8, 22))
                    .termsAccepted(true)
                    .profilePictureUrl(null)
                    .build()
            );

            userRepository.save(User.builder()
                    .name("Caio Pereira")
                    .email("caiopereiraaa19@gmail.com")
                    .password(passwordEncoder.encode("rsjssenha4055"))
                    .cpf("48520695490")
                    .rg("11223344")
                    .phone("81987501006")
                    .address("Rua Padre Alencar")
                    .number("256")
                    .complement("casa")
                    .neighborhood("Santo Amaro")
                    .city("Recife")
                    .state("PE")
                    .zipCode("50070000")
                    .gender(Gender.MALE)
                    .dateOfBirth(LocalDate.of(1988, 11, 30))
                    .termsAccepted(true)
                    .profilePictureUrl(null)
                    .build()
            );

        };
    }
}