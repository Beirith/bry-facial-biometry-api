package com.isaquebeirith.bry_facial_biometry_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationResponseDTO {
    private boolean matches;
    private float similarityScore;
}
