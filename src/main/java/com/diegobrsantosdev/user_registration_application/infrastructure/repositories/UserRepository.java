package com.diegobrsantosdev.user_registration_application.infrastructure.repositories;

import com.diegobrsantosdev.user_registration_application.infrastructure.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>{

    Optional<User> findByEmail(String email);

    @Transactional
    void deleteByEmail(String email);


}
