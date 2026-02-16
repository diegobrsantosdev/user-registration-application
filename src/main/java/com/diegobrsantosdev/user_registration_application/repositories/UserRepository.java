package com.diegobrsantosdev.user_registration_application.repositories;

import com.diegobrsantosdev.user_registration_application.models.Role;
import com.diegobrsantosdev.user_registration_application.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByCpf(String cpf);
    Optional<User> findByEmail(String email);

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByRg(String rg);

    @Transactional
    void deleteById(Integer id);

    long countByRoles(Role role);


}
