package com.isaquebeirith.bry_facial_biometry_api.service;

import com.isaquebeirith.bry_facial_biometry_api.model.FacialTemplate;
import com.isaquebeirith.bry_facial_biometry_api.model.User;
import com.isaquebeirith.bry_facial_biometry_api.repository.FacialTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacialTemplateService {
    private final FacialTemplateRepository facialTemplateRepository;

    public void createFacialTemplate(User user) {

    }

    public List<FacialTemplate> findAll() {
        return facialTemplateRepository.findAll();
    }
}
