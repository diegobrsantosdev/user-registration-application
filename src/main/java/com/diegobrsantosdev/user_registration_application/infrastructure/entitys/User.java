package com.diegobrsantosdev.user_registration_application.infrastructure.entitys;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@Entity

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

   @Column(name = "email", unique = true)
    private String email;

   @Column(name = "name")
    private String name;

}
