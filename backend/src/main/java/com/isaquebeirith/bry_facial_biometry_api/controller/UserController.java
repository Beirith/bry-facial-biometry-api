package com.isaquebeirith.bry_facial_biometry_api.controller;

import com.isaquebeirith.bry_facial_biometry_api.dto.*;
import com.isaquebeirith.bry_facial_biometry_api.exception.PictureReadException;
import com.isaquebeirith.bry_facial_biometry_api.model.User;
import com.isaquebeirith.bry_facial_biometry_api.service.UserBatchService;
import com.isaquebeirith.bry_facial_biometry_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserBatchService userBatchService;

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
    public ResponseEntity<UserResponseDTO> create(@Valid UserCreateDTO request)  {
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setCpf(request.getCpf());

        byte[] pictureBytes = getPictureBytes(request.getPicture());

        newUser.setPicture(pictureBytes);

        User result = userService.create(newUser);

        UserResponseDTO response = UserResponseDTO.fromEntity(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid UserUpdateDTO request)  {
        String newName = request.getName();
        byte[] newPicture = null;

        if (request.getPicture() != null && !request.getPicture().isEmpty()) {
            newPicture = getPictureBytes(request.getPicture());
        }

        User updatedUser = userService.update(id, newName, newPicture);

        UserResponseDTO response = UserResponseDTO.fromEntity(updatedUser);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/batch", consumes = "multipart/form-data")
    public ResponseEntity<List<UserBatchResultDTO>> createBatch(
            @RequestPart("users") List<UserCreateBatchDTO> users,
            @RequestPart("pictures") List<MultipartFile> pictures) {
        if (users.size() != pictures.size()) {
            throw new RuntimeException("A quantidade de usuários não corresponde à quantidade de fotos enviadas.");
        }

        List<byte[]> pictureBytesList = new ArrayList<>();
        for (MultipartFile picture : pictures) {
            pictureBytesList.add(getPictureBytes(picture));
        }

        List<UserBatchResultDTO> response = userBatchService.createBatch(users, pictureBytesList);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        userService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private byte[] getPictureBytes(MultipartFile pictureFile) {
        byte[] pictureBytes;

        try {
            pictureBytes = pictureFile.getBytes();
        } catch (Exception e) {
            throw new PictureReadException("Erro ao ler o arquivo de imagem do usuário.");
        }

        return  pictureBytes;
    }
}
