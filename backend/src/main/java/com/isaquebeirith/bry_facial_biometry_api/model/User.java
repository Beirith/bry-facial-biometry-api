package com.isaquebeirith.bry_facial_biometry_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "É obrigatório informar o nome do usuário.")
    private String name;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "É obrigatório informar o CPF do usuário.")
    private String cpf;

    @Lob
    @Column(nullable = false)
    @NotNull(message = "É obrigatório anexar uma foto do usuário.")
    private byte[] picture;
}
