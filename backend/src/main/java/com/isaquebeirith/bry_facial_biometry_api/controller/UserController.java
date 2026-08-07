package com.isaquebeirith.bry_facial_biometry_api.controller;

import com.isaquebeirith.bry_facial_biometry_api.dto.UserCreateDTO;
import com.isaquebeirith.bry_facial_biometry_api.dto.UserResponseDTO;
import com.isaquebeirith.bry_facial_biometry_api.dto.UserUpdateDTO;
import com.isaquebeirith.bry_facial_biometry_api.model.User;
import com.isaquebeirith.bry_facial_biometry_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<User> users = userService.findAll();

        List<UserResponseDTO> response = users.stream().map(UserResponseDTO::fromEntity).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        User user = userService.findById(id);

        UserResponseDTO response = UserResponseDTO.fromEntity(user);

        return ResponseEntity.ok(response);
    }

    @GetMapping(params = "cpf")
    public ResponseEntity<UserResponseDTO> findByCpf(@RequestParam String cpf) {
        User user = userService.findByCpf(cpf);

        UserResponseDTO response = UserResponseDTO.fromEntity(user);

        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<UserResponseDTO> create(@Valid UserCreateDTO request) throws IOException {
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setCpf(request.getCpf());
        newUser.setPicture(request.getPicture().getBytes());

        User result = userService.create(newUser);

        UserResponseDTO response = UserResponseDTO.fromEntity(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = "multipar/form-data")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid UserUpdateDTO request) throws IOException {
        String newName = request.getName();
        byte[] newPicture =  request.getPicture().getBytes();

        User updatedUser = userService.update(id, newName, newPicture);

        UserResponseDTO response = UserResponseDTO.fromEntity(updatedUser);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        userService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
