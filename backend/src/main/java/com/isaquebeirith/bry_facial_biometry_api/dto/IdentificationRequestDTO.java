package com.isaquebeirith.bry_facial_biometry_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class IdentificationRequestDTO {
    @NotNull(message = "É obrigatório anexar uma foto para verificação.")
    private MultipartFile picture;

    private Float threshold;
}