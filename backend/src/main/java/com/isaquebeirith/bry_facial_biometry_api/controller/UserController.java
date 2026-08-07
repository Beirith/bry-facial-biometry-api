package com.isaquebeirith.bry_facial_biometry_api.controller;

import com.isaquebeirith.bry_facial_biometry_api.dto.UserRequestDTO;
import com.isaquebeirith.bry_facial_biometry_api.dto.UserResponseDTO;
import com.isaquebeirith.bry_facial_biometry_api.model.User;
import com.isaquebeirith.bry_facial_biometry_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<UserResponseDTO> create(@Valid UserRequestDTO request) throws IOException {
        User user = new User();
        user.setName(request.getName());
        user.setCpf(request.getCpf());
        user.setPicture(request.getPicture().getBytes());

        User result = userService.create(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(result.getId());
        response.setName(result.getName());
        response.setCpf(result.getCpf());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}