package com.isaquebeirith.bry_facial_biometry_api.controller;

import com.isaquebeirith.bry_facial_biometry_api.dto.FacialTemplateResponseDTO;
import com.isaquebeirith.bry_facial_biometry_api.model.FacialTemplate;
import com.isaquebeirith.bry_facial_biometry_api.service.FacialTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/facial_templates")
@RequiredArgsConstructor
public class FacialTemplateController {
    private final FacialTemplateService facialTemplateService;

    @GetMapping
    public ResponseEntity<List<FacialTemplateResponseDTO>> findAll() {
        List<FacialTemplate> facialTemplates = facialTemplateService.findAll();

        List< FacialTemplateResponseDTO> response = facialTemplates.stream().
                map(FacialTemplateResponseDTO::fromEntity).toList();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
