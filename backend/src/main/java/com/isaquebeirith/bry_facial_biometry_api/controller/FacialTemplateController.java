package com.isaquebeirith.bry_facial_biometry_api.controller;

import com.isaquebeirith.bry_facial_biometry_api.dto.IdentificationRequestDTO;
import com.isaquebeirith.bry_facial_biometry_api.dto.IdentificationResponseDTO;
import com.isaquebeirith.bry_facial_biometry_api.dto.VerificationRequestDTO;
import com.isaquebeirith.bry_facial_biometry_api.dto.VerificationResponseDTO;
import com.isaquebeirith.bry_facial_biometry_api.exception.PictureReadException;
import com.isaquebeirith.bry_facial_biometry_api.service.FacialTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/facial-templates")
@RequiredArgsConstructor
public class FacialTemplateController {
    private final FacialTemplateService facialTemplateService;

    @PostMapping(value = "/verify", consumes = "multipart/form-data")
    public ResponseEntity<VerificationResponseDTO> verifyFacialTemplate(@Valid VerificationRequestDTO request) {
        byte[] pictureBytes = readPicture(request.getPicture());

        VerificationResponseDTO response = facialTemplateService.verifyFacialTemplate(request.getCpf(), pictureBytes);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/identify", consumes = "multipart/form-data")
    public ResponseEntity<IdentificationResponseDTO> identifyFacialTemplate(@Valid IdentificationRequestDTO request) {
        byte[] pictureBytes = readPicture(request.getPicture());

        IdentificationResponseDTO response = facialTemplateService.identifyFacialTemplate(pictureBytes);

        return ResponseEntity.ok(response);
    }

    private byte[] readPicture(MultipartFile picture) {
        byte[] pictureBytes;

        try {
            pictureBytes = picture.getBytes();
        } catch (Exception e) {
            throw new PictureReadException("Erro ao ler o arquivo de imagem do usuário.");
        }

        return pictureBytes;
    }
}
