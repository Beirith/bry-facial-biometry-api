package com.isaquebeirith.bry_facial_biometry_api.dto;

import com.isaquebeirith.bry_facial_biometry_api.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String name;
    private String cpf;

    public static UserResponseDTO fromEntity(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setCpf(user.getCpf());

        return dto;
    }
}
