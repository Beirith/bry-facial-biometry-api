package com.isaquebeirith.bry_facial_biometry_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateBatchDTO {
    @NotBlank(message = "É obrigatório informar o nome do usuário.")
    private String name;

    @NotBlank(message = "É necessário informar o CPF do usuário.")
    private String cpf;
}
