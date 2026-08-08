package com.isaquebeirith.bry_facial_biometry_api.controller;

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

@RestController
@RequestMapping("/api/facial-templates")
@RequiredArgsConstructor
public class FacialTemplateController {
    private final FacialTemplateService facialTemplateService;

    @PostMapping(value = "/verify", consumes = "multipart/form-data")
    public ResponseEntity<VerificationResponseDTO> verifyFacialTemplate(@Valid VerificationRequestDTO request) {
        byte[] pictureBytes = null;

        try {
            pictureBytes = request.getPicture().getBytes();
        } catch (Exception e) {
            throw new PictureReadException("Erro ao ler o arquivo de imagem do usuário.");
        }

        VerificationResponseDTO response = facialTemplateService.verifyFacialTemplate(request.getCpf(), pictureBytes);

        return ResponseEntity.ok(response);
    }
}
