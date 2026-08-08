package com.isaquebeirith.bry_facial_biometry_api.service;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import com.isaquebeirith.bry_facial_biometry_api.biometry.generation.FacialTemplateGenerator;
import com.isaquebeirith.bry_facial_biometry_api.model.FacialTemplate;
import com.isaquebeirith.bry_facial_biometry_api.model.User;
import com.isaquebeirith.bry_facial_biometry_api.repository.FacialTemplateRepository;
import com.isaquebeirith.bry_facial_biometry_api.util.FeatureVectorConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class FacialTemplateService {
    private final FacialTemplateRepository facialTemplateRepository;
    private final FacialTemplateGenerator facialTemplateGenerator;

    public void createFacialTemplate(User user) throws Exception {
        FacialTemplate newFacialTemplate = new FacialTemplate();

        newFacialTemplate.setUser(user);

        byte[] pictureBytes = user.getPicture();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pictureBytes);
        Image image = ImageFactory.getInstance().fromInputStream(inputStream);

        float[] floatVector = facialTemplateGenerator.generate(image);
        byte[] featureVector = FeatureVectorConverter.toByteArray(floatVector);

        newFacialTemplate.setFeatureVector(featureVector);

        facialTemplateRepository.save(newFacialTemplate);
    }
}
