package com.isaquebeirith.bry_facial_biometry_api.dto;

import com.isaquebeirith.bry_facial_biometry_api.model.FacialTemplate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacialTemplateResponseDTO {
    private long id;
    private String name;
    private byte[] featureVector;

    public static FacialTemplateResponseDTO fromEntity(FacialTemplate facialTemplate) {
        FacialTemplateResponseDTO dto = new FacialTemplateResponseDTO();
        dto.setId(facialTemplate.getId());
        dto.setName(facialTemplate.getUser().getName());
        dto.setFeatureVector(facialTemplate.getFeatureVector());

        return dto;
    }
}
