package com.isaquebeirith.bry_facial_biometry_api.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserUpdateDTO {
    private String name;

    private MultipartFile picture;
}
