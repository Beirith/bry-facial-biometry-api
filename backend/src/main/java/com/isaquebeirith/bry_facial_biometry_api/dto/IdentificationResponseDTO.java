package com.isaquebeirith.bry_facial_biometry_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdentificationResponseDTO {
    private boolean identified;
    private UserResponseDTO user;
    private float score;
}