package com.isaquebeirith.bry_facial_biometry_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBatchResultDTO {
    private boolean success;
    private String name;
    private String cpf;
    private String error;
}
