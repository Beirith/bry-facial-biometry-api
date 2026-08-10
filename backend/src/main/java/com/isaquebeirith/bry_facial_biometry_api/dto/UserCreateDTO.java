package com.isaquebeirith.bry_facial_biometry_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserCreateDTO {
    @NotBlank(message = "É obrigatório informar o nome do usuário.")
    private String name;

    @NotBlank(message = "É obrigatório informar o CPF do usuário.")
    private String cpf;

    @NotNull(message = "É obrigatório anexar uma foto do usuário.")
    private MultipartFile picture;
}
