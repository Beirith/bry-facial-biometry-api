package com.isaquebeirith.bry_facial_biometry_api.service;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import com.isaquebeirith.bry_facial_biometry_api.biometry.generation.FacialTemplateGenerator;
import com.isaquebeirith.bry_facial_biometry_api.exception.FeatureVectorGenerationException;
import com.isaquebeirith.bry_facial_biometry_api.model.FacialTemplate;
import com.isaquebeirith.bry_facial_biometry_api.model.User;
import com.isaquebeirith.bry_facial_biometry_api.repository.FacialTemplateRepository;
import com.isaquebeirith.bry_facial_biometry_api.util.FeatureVectorConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacialTemplateService {
    private final FacialTemplateRepository facialTemplateRepository;
    private final FacialTemplateGenerator facialTemplateGenerator;

    public void createFacialTemplate(User user)  {
        FacialTemplate newFacialTemplate = new FacialTemplate();
        newFacialTemplate.setUser(user);

        byte[] featureVector = generateFeatureVector(user);

        newFacialTemplate.setFeatureVector(featureVector);

        facialTemplateRepository.save(newFacialTemplate);
    }

    public void updateFacialTemplate(User user)  {
        Optional<FacialTemplate> oldFacialTemplate = facialTemplateRepository.findByUserId(user.getId());

        if (oldFacialTemplate.isPresent()) {
            FacialTemplate template = oldFacialTemplate.get();
            byte[] featureVector = generateFeatureVector(user);
            template.setFeatureVector(featureVector);
            facialTemplateRepository.save(template);
        } else  {
            createFacialTemplate(user);
        }
    }

    private byte[] generateFeatureVector(User user)  {
        byte[] pictureBytes = user.getPicture();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pictureBytes);

        Image image;
        try {
            image = ImageFactory.getInstance().fromInputStream(inputStream);
        } catch (Exception e) {
            throw new FeatureVectorGenerationException("Erro ao gerar vetor de características a partir da foto.");
        }

        float[] floatVector = facialTemplateGenerator.generate(image);

        return FeatureVectorConverter.toByteArray(floatVector);
    }
}
