package com.diegobrsantosdev.user_registration_application.business;

import com.diegobrsantosdev.user_registration_application.infrastructure.entitys.User;
import com.diegobrsantosdev.user_registration_application.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void saveUser(User user){
        repository.saveAndFlush(user);
    }

    public User searchUserByEmail(String email){
        return repository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("Email not found")
        );
    }

    public void deleteUserByEmail(String email){
        repository.deleteByEmail(email);
    }


    public void updateUserById(Integer id, User user){
        User userEntity = repository.findById(id).orElseThrow(() ->
                new RuntimeException("User not found"));
        User userUpdated = User.builder()
                .email(user.getEmail() != null ?
                        user.getEmail() : userEntity.getEmail())
                .name(user.getName() != null ?
                        user.getName() : userEntity.getName())
                .id(userEntity.getId())
                .build();

        repository.saveAndFlush(userUpdated);

    }


}
